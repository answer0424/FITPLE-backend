package com.lec.spring.base.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lec.spring.base.domain.Gym;
import com.lec.spring.base.domain.HBTI;
import com.lec.spring.base.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
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

        // Gym 객체를 JSON 문자열로 변환
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String gymJson = objectMapper.writeValueAsString(user.getGym());
            String hbtiJson = objectMapper.writeValueAsString(user.getHbti()); // 수정된 부분

            String formattedBirth = null;
            if (user.getBirth() != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
                formattedBirth = formatter.format(user.getBirth());
            }

            // JWT 생성 (Payload 에 저장될 정보)
            return Jwts.builder()
                    .claim("id", user.getId())
                    .claim("username", user.getUsername())
                    .claim("email", user.getEmail())
                    .claim("nickname", user.getNickname())
                    .claim("birth", formattedBirth)
                    .claim("authority", user.getAuthority())
                    .claim("provider", user.getProvider())
                    .claim("address", user.getAddress())
                    .claim("gym", gymJson)
                    .claim("hbti", hbtiJson)
                    .claim("profileImg", user.getProfileImage())
                    .setIssuedAt(new Date(System.currentTimeMillis()))  // 생성 시기
                    .setExpiration(new Date(System.currentTimeMillis() + expiredMs))    // 만료 시간
                    .signWith(secretKey)
                    .compact();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return null;
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

    // email 확인
    public String getEmail(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);
    }

    // nickname 확인
    public String getNickname(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("nickname", String.class);
    }

    // birth 확인
    public String getBirth(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("birth", String.class);
    }

    // address 확인
    public String getAddress(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("address", String.class);
    }

    // provider 확인
    public String getProvider(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("provider", String.class);
    }

    // gym 확인
    public Gym getGym(String token) {
        try {
            // JWT에서 Claims 추출
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Gym 정보를 JSON 문자열로 추출
            String gymJson = claims.get("gym", String.class);

            // JSON 문자열을 Gym 객체로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            Gym gym = objectMapper.readValue(gymJson, Gym.class);

            return gym;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    // HBTI 확인
    public HBTI getHbti (String token) {
        try {
            // JWT에서 Claims 추출
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Gym 정보를 JSON 문자열로 추출
            String hbtiJson = claims.get("hbti", String.class);

            // JSON 문자열을 Gym 객체로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            HBTI hbti = objectMapper.readValue(hbtiJson, HBTI.class);

            return hbti;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
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

    // profileImage 확인
    public String getProfileImage(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("profileImg", String.class);
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
