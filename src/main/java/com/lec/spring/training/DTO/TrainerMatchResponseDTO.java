package com.lec.spring.training.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerMatchResponseDTO {
    private Long trainerId;
    private String trainerName;  // username
    private String nickname;
    private String hbti;
    private String gymName;
    private String profileImage;
    private Integer matchScore;
}
