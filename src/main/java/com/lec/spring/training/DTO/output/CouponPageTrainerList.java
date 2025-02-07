package com.lec.spring.training.DTO.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponPageTrainerList {
    long userId;
    String nickname;
    String profileImage;
    String gymName;
}
