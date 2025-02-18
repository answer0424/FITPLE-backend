package com.lec.spring.training.DTO.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerIdStudentIdDTO {
    Long studentId;
    Long trainerId;
    int times;
}
