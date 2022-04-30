package com.dou.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;

@Configuration
public class RedisConf {

    @Bean
    public RedisTemplate<String, Serializable> getRedis(RedisConnectionFactory factory) {
        RedisTemplate<String, Serializable> res = new RedisTemplate<>();
        res.setConnectionFactory(factory);
        res.setKeySerializer(new StringRedisSerializer());
        Jackson2JsonRedisSerializer<Object> objectJackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        res.setValueSerializer(objectJackson2JsonRedisSerializer);
        return res;
    }
}
