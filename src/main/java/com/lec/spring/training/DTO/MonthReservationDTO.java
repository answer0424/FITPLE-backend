package com.lec.spring.training.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthReservationDTO {
    long reservationId;
    long userId;
    String nickname;
    LocalDateTime date;
}
