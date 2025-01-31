package com.lec.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Path;

@Configuration
public class JsonConfig {

    @Bean
    public Path hbtiDataPath() throws Exception {
        // JSON 파일을 ClassPathResource로 로드하여 Path로 변환
        return new ClassPathResource("static/data/HbtiData.json").getFile().toPath();
    }
}
