package com.dou.redis;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

@Api
@RestController
@RequestMapping(path = "/redis")
public class RedisController {
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @ApiOperation(value = "测试redis", httpMethod = "GET")
    @RequestMapping("/set_key")
    public boolean test_redis_set_key(String key, String val) {
        ValueOperations<String, Serializable> opsVal = redisTemplate.opsForValue();
        opsVal.set(key, val);
        return true;
    }

    @ApiOperation(value = "测试redis", httpMethod = "GET")
    @RequestMapping("/get_key")
    public String test_redis_get_key(String key) {
        ValueOperations<String, Serializable> opsVal = redisTemplate.opsForValue();
        return (String) opsVal.get(key);
    }
}
