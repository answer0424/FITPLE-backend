package com.lec.spring.base.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
@Service
public class EmailAuthServiceImpl implements EmailAuthService {


    private static final String PREFIX = "auth-code:";
    private final RedisTemplate redisTemplate;

    public EmailAuthServiceImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    // 인증 번호 저장
    @Override
    public void storeAuthCode(String email, String authCode) {
       try {      //3분 유효시간
            redisTemplate.opsForValue().set(PREFIX + email, authCode, 3, TimeUnit.MINUTES);

            //저장
        }catch (Exception e) {
           e.printStackTrace();
       }
    }

    @Override
    public String getAuthCode(String email) {
        String result = (String)redisTemplate.opsForValue().get(PREFIX + email);
        return result;
    }

    //인증번호 삭제
    @Override
    public void deletedAuthCode(String email) {
        Boolean result = redisTemplate.delete(PREFIX + email);
    }


}// end EmailAuthServiceImpl
