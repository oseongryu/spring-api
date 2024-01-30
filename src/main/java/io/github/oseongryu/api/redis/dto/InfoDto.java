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
}

