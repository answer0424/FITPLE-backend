package com.lec.spring.training.DTO.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponPageTrainerList {
    long trainerId;
    String nickname;
    String profileImage;
    String gymName;
    int coupons;
    Integer times;
}
