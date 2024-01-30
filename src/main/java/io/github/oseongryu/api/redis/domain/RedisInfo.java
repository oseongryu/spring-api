package io.github.oseongryu.api.redis.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;


public class RedisInfo {

    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Response {
        private String duplicated;
        private String _class;
        private String useCnt;
        private String expiration;
        private String dvcId;
        private String usrId;
        private String expDttm;
        private String accToken;
        private String refreshToken;
    }
}