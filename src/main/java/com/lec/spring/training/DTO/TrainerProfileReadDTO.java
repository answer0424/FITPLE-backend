package com.lec.spring.training.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class TrainerProfileReadDTO {
    private Long id;
    private Long trainerId;
    private String trainerName;
    private String trainerEmail;
    private String trainerProfileImage;
    private Integer perPrice;
    private String content;
    private LocalDate career;
    private String isAccess;
    private List<CertificationDTO> certifications;
    private String hbti;
    private String gymName;
    private String gymAddress;
    private Double gymLatitude;
    private Double gymLongitude;
}
