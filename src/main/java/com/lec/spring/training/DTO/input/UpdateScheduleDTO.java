package com.lec.spring.training.DTO.input;

import lombok.Data;

@Data
public class UpdateScheduleDTO {
    String status;
    Long reservationId;
}
