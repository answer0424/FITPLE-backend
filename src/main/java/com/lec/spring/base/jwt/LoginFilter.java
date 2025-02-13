package com.lec.spring.base.jwt;

import com.lec.spring.base.config.PrincipalDetails;
import com.lec.spring.base.domain.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    // 인증(authrntication) 을 진행하는 메소드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // 클라이언트로부터 넘어온 username, password 값.
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        Authentication token = new UsernamePasswordAuthenticationToken(username.toUpperCase(), password, null);

        return authenticationManager.authenticate(token);  // 인증진행.
    }

    // 로그인 인증 성공 시 실행되는 메서드 (JWT 발급)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        // 인증이 끝난 객체 UserDetails 객체 가져오기
        PrincipalDetails userDetails = (PrincipalDetails) authResult.getPrincipal();

        // JWT 에 담을 내용
        User user = userDetails.getUser();

        // JWT 생성 - 만료 시간 1일 (24시간)로 설정
        long expirationTimeInMs = 86400000; // 1일
        String token = jwtUtil.generateToken(user, expirationTimeInMs);

        // Authorization 헤더에 JWT 를 담아서 보내기
        response.addHeader("Authorization", "Bearer " + token);

        String jsonResponse = String.format(
                "{\"authorities\":[{\"authority\":\"ROLE_TRAINER\"}],\"details\":null,\"authenticated\":true," +
                        "\"principal\":{\"user\":{\"createdAt\":null,\"id\":%d,\"username\":\"%s\",\"email\":\"%s\",\"birth\":\"%s\",\"nickname\":\"%s\",\"profileImage\":\"%s\"}," +
                        "\"enabled\":true,\"password\":\"%s\",\"username\":\"%s\",\"credentialsNonExpired\":true,\"accountNonExpired\":true,\"accountNonLocked\":true," +
                        "\"authorities\":[{\"authority\":\"ROLE_TRAINER\"}]},\"credentials\":null,\"name\":\"%s\"}",
                user.getId(), user.getUsername(), user.getEmail(), user.getBirth(), user.getNickname(), user.getProfileImage(),
                user.getPassword(), user.getUsername(), user.getUsername()
        );


        response.getWriter().write(jsonResponse);
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        // 로그인 실패 시 401 응답
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
