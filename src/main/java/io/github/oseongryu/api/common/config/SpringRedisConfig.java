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

    @Value("${spring.profiles.active}")
    private String profile;

     @Value("${spring.redis.cluster.nodes}")
     private String[] nodes;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        if("prd".equals(profile)){
            RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(Arrays.asList(nodes));
            return new LettuceConnectionFactory(clusterConfiguration);
        } else  {
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
            redisStandaloneConfiguration.setHostName(host);
            redisStandaloneConfiguration.setPort(port);
            redisStandaloneConfiguration.setPassword(password);
            return new LettuceConnectionFactory(redisStandaloneConfiguration);
        }
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // redisTemplate.setKeySerializer(new StringRedisSerializer());
        // redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setKeySerializer(new GenericToStringSerializer<>(Object.class)); // 키의 직렬화 설정
        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Object.class)); // 값의 직렬화 설정

        redisTemplate.setConnectionFactory(lettuceConnectionFactory());
        return redisTemplate;
    }
}