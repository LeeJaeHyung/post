package com.web.post.global.service;


import com.google.gson.Gson;
import com.web.post.global.dto.LoginUserDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisLoginTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final Gson gson;

    public RedisLoginTokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.gson = new Gson();
    }

    public void save(String token, LoginUserDto user) {
        String json = gson.toJson(user);
        Long userId = user.getId();
        redisTemplate.opsForValue().set("access_token:" + token, json, Duration.ofMinutes(30));
        String beforeToken = redisTemplate.opsForValue().get("user_id:" + userId);
        if (beforeToken != null) {
            redisTemplate.delete("access_token:"+beforeToken);
        }
        redisTemplate.opsForValue().set("user_id:" + userId, token, Duration.ofMinutes(30));

    }

    public LoginUserDto get(String token) {
        String json = redisTemplate.opsForValue().get("access_token:" + token);
        if (json == null) return null;
        return gson.fromJson(json, LoginUserDto.class);
    }

    public void remove(String token, Long memberId) {
        redisTemplate.delete("access_token:"+token);
        redisTemplate.delete("user_id:" + memberId);
    }
}
