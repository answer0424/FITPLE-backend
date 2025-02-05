package com.lec.spring.training.DTO.input;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateProfileImage {
    Long userId;
    String AIPrompt;
}
