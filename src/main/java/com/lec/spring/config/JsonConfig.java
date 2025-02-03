package com.lec.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class JsonConfig {

    @Bean
    public String hbtiJsonContent() throws IOException {
        // InputStream을 사용하여 JSON 파일을 문자열로 변환
        ClassPathResource resource = new ClassPathResource("static/data/HbtiData.json");
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
