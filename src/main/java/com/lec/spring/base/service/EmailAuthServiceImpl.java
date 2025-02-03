package com.lec.spring.base.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
@Service
public class EmailAuthServiceImpl implements EmailAuthService {


    // auth-code:cooconut0226@gmail.com <- 이런식으로 authCode가 작성이 된다.(key)
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
            System.out.println("성공적으로 저장되었습니다 : " + email + "인증번호 : " + authCode);
        }catch (Exception e) {
           e.printStackTrace();
           System.out.println("실패 : " + e.getMessage());
       }
    }

    @Override
    public String getAuthCode(String email) {
        String result = (String)redisTemplate.opsForValue().get(PREFIX + email);
        System.out.println("##########인증번호 가져오기 : " + result);
        return result;
    }

    //인증번호 삭제
    @Override
    public void deletedAuthCode(String email) {
        Boolean result = redisTemplate.delete(PREFIX + email);
        System.out.println("###########인증번호가 삭제되었나요 ? :" +result);
    }


}// end EmailAuthServiceImpl
