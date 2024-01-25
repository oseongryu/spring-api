package io.github.oseongryu.api.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableRedisRepositories
public class SpringRedisConfig {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

//    @Bean
//    public LettuceConnectionFactory lettuceConnectionFactory() {
//        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(Arrays.asList("127.0.0.1:6379", "127.0.0.1:6380", "127.0.0.1:6381")); // 클러스터 노드 설정
//        return new LettuceConnectionFactory(clusterConfiguration);
//    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // redisTemplate.setKeySerializer(new StringRedisSerializer());
        // redisTemplate.setValueSerializer(new StringRedisSerializer());
       redisTemplate.setKeySerializer(new GenericToStringSerializer<>(Object.class)); // 키의 직렬화 설정
       redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Object.class)); // 값의 직렬화 설정

//       if(hosts.length == 1) {
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
            redisStandaloneConfiguration.setHostName(host);
            redisStandaloneConfiguration.setPort(port);
            redisStandaloneConfiguration.setPassword(password);
            LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
            redisTemplate.setConnectionFactory(lettuceConnectionFactory);
//       } else {
//           redisTemplate.setConnectionFactory(lettuceConnectionFactory());
//       }

        return redisTemplate;
    }
}