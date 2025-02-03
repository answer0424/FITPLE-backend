package com.lec.spring.training.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReservationDTO {
    LocalDateTime date;
    Long trainingId;
}
