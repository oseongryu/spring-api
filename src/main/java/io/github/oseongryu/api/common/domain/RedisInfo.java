package io.github.oseongryu.api.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

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

    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Users {
        private String usrId;
        private Integer length;

    }
}