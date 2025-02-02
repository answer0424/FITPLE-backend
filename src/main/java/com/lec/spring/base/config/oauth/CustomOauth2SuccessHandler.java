package com.lec.spring.base.config.oauth;

import com.lec.spring.base.config.PrincipalDetails;
import com.lec.spring.base.domain.User;
import com.lec.spring.base.jwt.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomOauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${app.oauth2.auth-redirect-uri}")
    private String authRedirectUri;

    private final JWTUtil jwtUtil;

    public CustomOauth2SuccessHandler(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // Oauth2 인증 성공하면 수행되는 메서드

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("CustomOauth2SuccessHandler onAuthenticationSuccess() 호출");

        // OAuth2USer 상속받는 객체
        PrincipalDetails oauth2User = (PrincipalDetails) authentication.getPrincipal();

        // JWT에 담을 내용
        User user = oauth2User.getUser();
        long expirationTimeInMs = 2000 * 60; // 2분

        Collection<? extends GrantedAuthority> authorities = oauth2User.getAuthorities();
        String role = authorities.iterator().next().getAuthority();

        // JWT 생성
        String token = jwtUtil.generateToken(user, expirationTimeInMs);
        System.out.println("oauth 인증 token: " + token);

        // cookie에 담기
        response.addCookie(createCookie("accessToken", token));

        getRedirectStrategy().sendRedirect(request, response, authRedirectUri);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 60);
        return cookie;
    }
}
