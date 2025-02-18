package com.lec.spring.base.DTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrainerInfoDTO {
    private Long trainerId;
    private String email;
    private String name;
    private int remainingSessions;
}
