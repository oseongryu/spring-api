package io.github.oseongryu.api.common.config.jpa;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class AuditorAwareConfig {

    @Value("${app.local.user-id}")
    private String propUserId;

    @Bean
    public AuditorAware auditorAware() {
        return () -> {
            String userId = propUserId;
            return Optional.of(userId);
        };
    }

}
