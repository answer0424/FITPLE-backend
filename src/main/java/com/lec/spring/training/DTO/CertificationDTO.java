package com.lec.spring.training.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CertificationDTO {
    private String imageUrl; // 이미지 파일 URL (credentials 필드에서 가져옴)
    private String skills;
}
