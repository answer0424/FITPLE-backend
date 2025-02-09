package com.lec.spring.training.DTO;

import com.lec.spring.training.domain.ReservationStatus;
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
    ReservationStatus status;
}
