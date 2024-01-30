package io.github.oseongryu.api.redis.controller;


import io.github.oseongryu.api.redis.domain.ResponseData;
import io.github.oseongryu.api.redis.dto.InfoDto;
import io.github.oseongryu.api.redis.service.InfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@RestController
@RequestMapping(path = io.github.oseongryu.api.redis.controller.RedisRestController.REQUEST_BASE_PATH)
public class RedisRestController {
    static final String REQUEST_BASE_PATH = "api";

	@Autowired
    @Lazy
	RedisTemplate<String, String> redisTemplate;

	@Autowired
	InfoService infoService;


	@RequestMapping(value = { "select",  }, method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity select(InfoDto.RedisInfo redisInfo) {
        try {
            getString("CERT_NO");
        } catch(Exception exception){
            log.debug(exception.getMessage());
        }
        finally {
        }
        return ResponseEntity.ok("select");
    }

    @RequestMapping(value = { "users" }, method = {RequestMethod.GET})
    public ResponseEntity users() {
		ResponseData<List<InfoDto.Response>> response = new ResponseData<>();
        try {
            response = retrieveData("ustraJwt:usrId:*", false);
			// expDttm 기준으로 Response 객체 정렬
        	Collections.sort(response.getList(), Comparator.comparing(InfoDto.Response::getKeyLength).reversed());

//			for (InfoDto.Response info : response.getList()) {
//				UsersDto.Save data =  UsersDto.Save.builder()
//						.usrId(info.getUsrId())
//						.tokenLength(info.getLength())
//						.build();
//				infoService.insert(data);
//			}

        } catch(Exception exception){
            log.debug(exception.getMessage());
        }
        finally {
        }
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = { "keyword" }, method = {RequestMethod.POST})
    public ResponseEntity keyword(@RequestBody Map<String, Object> paramMap) {
		String keyword = paramMap.get("keyword").toString();

		ResponseData<List<InfoDto.Response>> response = new ResponseData<>();
        try {
            response = retrieveData(keyword, false);
			// expDttm 기준으로 Response 객체 정렬
        	Collections.sort(response.getList(), Comparator.comparing(InfoDto.Response::getKeyLength).reversed());

//			for (InfoDto.Response info : response.getList()) {
//				InfoDto.Save data =  InfoDto.Save.builder()
//						.key(info.getKey())
//						.keyLength(info.getKeyLength())
//						.build();
//				infoService.insert(data);
//			}

        } catch(Exception exception){
            log.debug(exception.getMessage());
        }
        finally {
        }
        return ResponseEntity.ok(response);
    }


	public ResponseData<List<InfoDto.Response>> retrieveData(String keyword, Boolean checkSkip) {
		ResponseData<List<InfoDto.Response>> response = new ResponseData<>();
		ResponseData<List<InfoDto.RedisInfo>> users = new ResponseData<>();
        Set<String> keys = redisTemplate.keys(keyword);
		List<InfoDto.Response> mapInfo = new ArrayList<>();
        for (String key : keys) {
			if(!checkSkip){
				users = boundSetOps(key, true);
			}
			int cnt = 0;
			if(users.getLength() == null) {
				cnt = 0;
			} else {
				cnt = users.getLength();
			}

            // 타입 확인
			int length = 0;
            String type = redisTemplate.type(key).name().toUpperCase();
            // 사이즈 확인
            if (type.equals("HASH")) {
                // Hash 값 가져오기
                HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
                Map<String, Object> hash = hashOperations.entries(key);
				length = hash.size();
//                for (String field : hash.keySet()) {
//                    String data = hash.get(field).toString();
//					length = data.length();
//                }

            } else if (type.equals("SET")) {
                // Set 값 가져오기
                SetOperations<String, String> setOperations = redisTemplate.opsForSet();
                Set<String> set = setOperations.members(key);
                length = set.size();
            } else {
                // String 값 가져오기
                Object data = redisTemplate.opsForValue().get(key);
                length  = ((String) data).length();
            }


			InfoDto.Response user = InfoDto.Response.builder().key(key).keyLength((long) length).build();
			mapInfo.add(user);
        }
		response.setLength(keys.size());
		response.setList(mapInfo);
		return response;
    }

    @RequestMapping(value = { "user/{usrId}" }, method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity userAll(@PathVariable String usrId) {
		ResponseData<List<InfoDto.RedisInfo>> response = new ResponseData<>();
        try {
            response = boundSetOps("ustraJwt:usrId:" + usrId, false);
			// expDttm 기준으로 Response 객체 정렬
        	Collections.sort(response.getList(), Comparator.comparing(InfoDto.RedisInfo::getExpDttm));
        } catch(Exception exception){
            log.debug(exception.getMessage());
        }
        finally {
        }
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = { "user/{usrId}/{hashKey}" }, method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity user(@PathVariable String usrId, @PathVariable String hashKey ) {
		ResponseData<List<String>> response = new ResponseData<>();
        try {
            response = boundSetOps("ustraJwt:usrId:" + usrId, hashKey, false);
        } catch(Exception exception){
            log.debug(exception.getMessage());
        }
        finally {
        }
        return ResponseEntity.ok(response);
    }


	public ResponseData<List<InfoDto.RedisInfo>> boundSetOps(String keyword, Boolean dataSkip) {
		ResponseData<List<InfoDto.RedisInfo>> response = new ResponseData<>();

		BoundSetOperations<String, String> boundSetOps = redisTemplate.boundSetOps(keyword);
		Set<String> memberSet = boundSetOps.members();
        long STANDARD_TIME_STAMP = toDate(LocalDateTime.of(2023, 3, 28, 0, 0)).getTime();
		int index = -1;
		int size = memberSet.size();
		int delCount = 0;
		int delTotalCount = 0;
		int expiredCount = 0;
		int expiredTotalCount = 0;
		List<InfoDto.RedisInfo> mapInfo = new ArrayList<>();
		if(dataSkip) {

		} else {
			for (final String key : memberSet) {
				final String refreshTokenKey = "ustraJwt:" + key;
				final String refreshTokenIdxKey = "ustraJwt:" + key + ":idx";
				final String refreshPhantomKey = "ustraJwt:" + key + ":phantom";
				final Long timeStamp = Long.valueOf(key.substring(0, 13));
				index++;
				if (index % 1000 == 0) {
					try {
						log.debug("=== index:{}/{}, delete:{}/{}, expired:{}/{}, timestamp:{}", index, size, delCount, delTotalCount, expiredCount, expiredTotalCount, STANDARD_TIME_STAMP);
						delCount = 0; expiredCount=0; Thread.sleep(1);
					} catch (InterruptedException e) {
						log.error(e.getMessage(), e);
					}
				}

				// 이미 만료된 정보
				if (!redisTemplate.hasKey(refreshTokenKey)) {
	//				if(!simulation) boundSetOps.remove(key);
					// log.debug("=== index:{}, key:{} -> expired remove complete.", index, refreshTokenKey);
					// System.out.println("=== index:"+index+", key:"+refreshTokenKey+" -> expired remove complete.");
					expiredCount++; expiredTotalCount++;
					continue;
				} else {
					InfoDto.RedisInfo result =  getAllHashEntries(refreshTokenKey);
					mapInfo.add(result);
				}
				log.debug("=== complete info -> total:{}, delete:{}, expired:{}, set timestamp:{}", size, delTotalCount, expiredTotalCount, STANDARD_TIME_STAMP);
			}
		}

		response.setLength(memberSet.size());
		response.setList(mapInfo);
		return response;
	}

	public ResponseData<List<String>> boundSetOps(String keyword, String hashKey, Boolean dataSkip) {
		ResponseData<List<String>> response = new ResponseData<>();
		BoundSetOperations<String, String> boundSetOps = redisTemplate.boundSetOps(keyword);
		Set<String> memberSet = boundSetOps.members();
        long STANDARD_TIME_STAMP = toDate(LocalDateTime.of(2023, 3, 28, 0, 0)).getTime();
		int index = -1;
		int size = memberSet.size();
		int delCount = 0;
		int delTotalCount = 0;
		int expiredCount = 0;
		int expiredTotalCount = 0;
		List<String> mapInfo = new ArrayList<>();

		if(dataSkip) {

		} else {
			for (final String key : memberSet) {
				final String refreshTokenKey = "ustraJwt:" + key;
				final String refreshTokenIdxKey = "ustraJwt:" + key + ":idx";
				final String refreshPhantomKey = "ustraJwt:" + key + ":phantom";
				final Long timeStamp = Long.valueOf(key.substring(0, 13));
				index++;
				if (index % 1000 == 0) {
					try {
						log.debug("=== index:{}/{}, delete:{}/{}, expired:{}/{}, timestamp:{}", index, size, delCount, delTotalCount, expiredCount, expiredTotalCount, STANDARD_TIME_STAMP);
						delCount = 0;
						expiredCount = 0;
						Thread.sleep(1);
					} catch (InterruptedException e) {
						log.error(e.getMessage(), e);
					}
				}

				// 이미 만료된 정보
				if (!redisTemplate.hasKey(refreshTokenKey)) {
					//				if(!simulation) boundSetOps.remove(key);
					// log.debug("=== index:{}, key:{} -> expired remove complete.", index, refreshTokenKey);
					// System.out.println("=== index:"+index+", key:"+refreshTokenKey+" -> expired remove complete.");
					expiredCount++;
					expiredTotalCount++;
					continue;
				} else {
					if (hashKey != null) {
						String accToken = getHash(refreshTokenKey, hashKey);
						mapInfo.add(accToken);
					}

				}
				log.debug("=== complete info -> total:{}, delete:{}, expired:{}, set timestamp:{}", size, delTotalCount, expiredTotalCount, STANDARD_TIME_STAMP);
			}
		}
		response.setLength(memberSet.size());
		response.setList(mapInfo);
		return response;
	}

	public String getHash(String key, String hashKey) {
		HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
		String value = (String) hashOperations.get(key, hashKey);
		if(value != null){
			log.debug(value);
		}
		return value;
	}

	public InfoDto.RedisInfo getAllHashEntries(String key) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
		Map<String, String> map =  hashOperations.entries(key);
		String usrId = map.get("usrId");
		String accToken = map.get("accToken");
		String refreshToken = map.get("refreshToken");
		String expDttm = map.get("expDttm");
		InfoDto.RedisInfo redisInfo = InfoDto.RedisInfo.builder()
				.usrId(usrId)
				.accToken(accToken)
				.refreshToken(refreshToken)
				.expDttm(expDttm)
				.build();
        return redisInfo;
    }

    public String getString(String keyword) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		String value = (String) valueOperations.get(keyword);
		return value;
	}

    public Date toDate(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		return java.util.Date
			      .from(localDateTime.atZone(ZoneId.systemDefault())
			      .toInstant());
	}
}