package com.lec.spring.training.DTO.input;

import lombok.Data;

@Data
public class TrainerIdStudentIdDTO {
    Long studentId;
    Long trainerId;
    int times;
}
