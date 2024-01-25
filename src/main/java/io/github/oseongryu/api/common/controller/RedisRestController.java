package io.github.oseongryu.api.common.controller;


import io.github.oseongryu.api.common.domain.RedisInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@RestController
@RequestMapping(path = io.github.oseongryu.api.common.controller.RedisRestController.REQUEST_BASE_PATH)
public class RedisRestController {
    static final String REQUEST_BASE_PATH = "api";

	@Autowired
	RedisTemplate<String, String> redisTemplate;



	@RequestMapping(value = { "select",  }, method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity select(RedisInfo redisInfo) {
        try {
            getString("CERT_NO");
        } catch(Exception exception){
            log.debug(exception.getMessage());
        }
        finally {
        }
        return ResponseEntity.ok("select");
    }

    @RequestMapping(value = { "user/{usrId}" }, method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity userAll(@PathVariable String usrId) {
		List<RedisInfo.Response> result = new ArrayList<>();
        try {
            result = boundSetOps("ustraJwt:usrId:" + usrId);
			// expDttm 기준으로 Response 객체 정렬
        	Collections.sort(result, Comparator.comparing(RedisInfo.Response::getExpDttm));
        } catch(Exception exception){
            log.debug(exception.getMessage());
        }
        finally {
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = { "user/{usrId}/{hashKey}" }, method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity user(@PathVariable String usrId, @PathVariable String hashKey ) {
		List<String> result = new ArrayList<>();
        try {
            result = boundSetOps("ustraJwt:usrId:" + usrId, hashKey);
        } catch(Exception exception){
            log.debug(exception.getMessage());
        }
        finally {
        }
        return ResponseEntity.ok(result);
    }


	public List<RedisInfo.Response> boundSetOps(String keyword) {
		BoundSetOperations<String, String> boundSetOps = redisTemplate.boundSetOps(keyword);
		Set<String> memberSet = boundSetOps.members();
        long STANDARD_TIME_STAMP = toDate(LocalDateTime.of(2023, 3, 28, 0, 0)).getTime();
		int index = -1;
		int size = memberSet.size();
		int delCount = 0;
		int delTotalCount = 0;
		int expiredCount = 0;
		int expiredTotalCount = 0;
		List<RedisInfo.Response> mapInfo = new ArrayList<>();
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
				RedisInfo.Response result =  getAllHashEntries(refreshTokenKey);
				mapInfo.add(result);
			}
			log.debug("=== complete info -> total:{}, delete:{}, expired:{}, set timestamp:{}", size, delTotalCount, expiredTotalCount, STANDARD_TIME_STAMP);
        }

		return mapInfo;
	}

	public List<String> boundSetOps(String keyword, String hashKey) {
		BoundSetOperations<String, String> boundSetOps = redisTemplate.boundSetOps(keyword);
		Set<String> memberSet = boundSetOps.members();
        long STANDARD_TIME_STAMP = toDate(LocalDateTime.of(2023, 3, 28, 0, 0)).getTime();
		int index = -1;
		int size = memberSet.size();
		int delCount = 0;
		int delTotalCount = 0;
		int expiredCount = 0;
		int expiredTotalCount = 0;
		List<String> arrAccToken = new ArrayList<>();
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
				if(hashKey != null){
					String accToken = getHash(refreshTokenKey, hashKey);
					arrAccToken.add(accToken);
				}

			}
			log.debug("=== complete info -> total:{}, delete:{}, expired:{}, set timestamp:{}", size, delTotalCount, expiredTotalCount, STANDARD_TIME_STAMP);
        }

		return arrAccToken;
	}

	public String getHash(String key, String hashKey) {
		HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
		String value = (String) hashOperations.get(key, hashKey);
		if(value != null){
			log.debug(value);
		}
		return value;
	}

	public RedisInfo.Response getAllHashEntries(String key) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
		Map<String, String> map =  hashOperations.entries(key);
		String usrId = map.get("usrId");
		String accToken = map.get("accToken");
		String refreshToken = map.get("refreshToken");
		String expDttm = map.get("expDttm");
		RedisInfo.Response redisInfo = RedisInfo.Response.builder()
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