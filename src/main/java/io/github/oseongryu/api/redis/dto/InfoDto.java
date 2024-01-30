package io.github.oseongryu.api.redis.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

public class InfoDto {

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Save {
        private String key;
        private Long keyLength;
        private String keyType;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Response {
        private String key;
        private Long keyLength;
        private String keyType;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RedisInfo {
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

