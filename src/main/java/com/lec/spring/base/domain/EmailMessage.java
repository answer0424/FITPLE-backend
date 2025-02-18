package com.lec.spring.base.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class EmailMessage {
    //수신자
    private String to;
    // 메일 제목
    private String subject;
    // 메일 내용
    private String message;
}
