package cn.edu.hbwe.gogo.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    private final StringRedisTemplate stringRedisTemplate;

    public TokenService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void saveToken(String token, String uid) {
        stringRedisTemplate.opsForValue().set(uid, token, 10, TimeUnit.HOURS);
    }

    public String getToken(String uid) {
        return stringRedisTemplate.opsForValue().get(uid);
    }

    public void deleteToken(String uid) {
        stringRedisTemplate.delete(uid);
    }
}