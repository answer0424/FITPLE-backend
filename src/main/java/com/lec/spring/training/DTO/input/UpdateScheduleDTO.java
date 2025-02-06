package com.lec.spring.training.DTO.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateScheduleDTO {
    String status;
    Long reservationId;
}
