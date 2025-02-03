package com.lec.spring.base.jwt;

import com.lec.spring.base.domain.User;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class JWTUtil {
    private SecretKey secretKey;

    // JWT 서명 및 검증에 사용될 비밀 키를 초기화하는 역할
    public JWTUtil(@Value("${jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String generateToken(User user, Long expiredMs) {
        System.out.println("JWTUtil.generateToken() 호출");

        String formattedBirth = null;
        if (user.getBirth() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            formattedBirth = formatter.format(user.getBirth());
        }

        // JWT 생성 (Payload 에 저장될 정보)
        return Jwts.builder()
                .claim("id", user.getId())
                .claim("email", user.getEmail())
                .claim("username", user.getUsername())
                .claim("nickname", user.getNickname())
                .claim("birth", formattedBirth)
                .claim("authority", user.getAuthority())
                .setIssuedAt(new Date(System.currentTimeMillis()))  // 생성 시기
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))    // 만료 시간
                .signWith(secretKey)
                .compact();
    }

    // JWT token 에서 내용 확인
    // id 확인
    public Long getId(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id", Long.class);
    }

    // username 확인
    public String getUsername(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    // authority 확인
    public String getAuthority(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("authority", String.class);
    }

    // 만료 여부 확인
    public Boolean isExpired(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration().before(new Date());
    }

}
