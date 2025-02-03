package com.lec.spring.base.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class MvcConfiguraion implements WebMvcConfigurer {

    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")      // CORS 요청을 허용할 URL
                .allowedOrigins(allowedOrigins)     // 요청을 허용할 도메인
                .allowCredentials(true)            // 쿠키 요청 허용
                .allowedMethods("*")
                .allowedHeaders("*");


    }
}
