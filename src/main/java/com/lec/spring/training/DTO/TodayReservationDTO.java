package com.lec.spring.training.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodayReservationDTO {
    String nickname;
    String status;
    String profileImage;
    LocalDateTime date;
}
