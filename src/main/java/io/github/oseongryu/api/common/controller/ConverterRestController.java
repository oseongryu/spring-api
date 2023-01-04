package io.github.oseongryu.api.common.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.common.base.CaseFormat;
import io.github.oseongryu.api.common.dto.OriginConvertDto;
import io.github.oseongryu.api.common.dto.OriginDto;
import io.github.oseongryu.api.common.dto.OriginNamingDto;
import io.github.oseongryu.api.common.dto.OriginUnderDto;
import io.github.oseongryu.api.common.utility.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/common/converter")
@Slf4j
public class ConverterRestController {

    @Autowired
    public ModelMapper modelMapperCamelToUnder;

    @PostMapping("/v1/dto-to-map")
    public ResponseEntity DtoToMapVersion1(@RequestBody List<OriginDto> dtoList){
        List<Map<String,Object>> dtoToMapList = new ArrayList<Map<String,Object>>();
        for (int index = 0; index < dtoList.size() ; index++){

            OriginUnderDto result = modelMapperCamelToUnder.map(dtoList.get(index),  OriginUnderDto.class);
            ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            Map<String,Object> map = objectMapper.convertValue(result, HashMap.class);
            Map<String, Object> newMap = map.keySet().stream().collect(Collectors.toMap(key -> key.toUpperCase(), key -> StringUtil.checkStringNull(map.get(key))));
            dtoToMapList.add(newMap);
        }
        return ResponseEntity.ok(dtoToMapList);
    }

    @PostMapping("/v2/dto-to-map")
    public ResponseEntity dtoToMapVersion2(@RequestBody List<OriginConvertDto> dtoList){
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/v3/dto-to-map")
    public ResponseEntity dtoToMapVersion3(@RequestBody List<OriginConvertDto> dtoList){

//        List<Map<String,Object>> dtoToMapList = new ArrayList<Map<String,Object>>();
//        List<OriginConvertDto> temp = new ArrayList<>();
//        for (int index = 0; index < dtoList.size() ; index++){
//
//            OriginConvertDto result = modelMapperCamelToUnder.map(dtoList.get(index),  OriginConvertDto.class);
//            log.debug(String.valueOf(result));
//            temp.add(result);
//        }
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/v4/dto-to-map")
    public ResponseEntity dtoToMapVersion4(@RequestBody List<OriginNamingDto> dtoList){

        return ResponseEntity.ok(dtoList);
    }

    // https://stackoverflow.com/questions/1143951/what-is-the-simplest-way-to-convert-a-java-string-from-all-caps-words-separated
    @PostMapping("/v5/dto-to-map")
    public ResponseEntity dtoToMapVersion5(@RequestBody List<OriginNamingDto> dtoList){
        String temp = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "TEST_DATA");
        String temp2 = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, "testData");
        return ResponseEntity.ok(temp2);
    }

}
