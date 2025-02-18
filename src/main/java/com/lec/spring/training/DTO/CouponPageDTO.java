package com.lec.spring.training.DTO;

import com.lec.spring.base.domain.User;
import com.lec.spring.training.DTO.output.CouponPageTrainerList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CouponPageDTO {
    int coupons;

    Integer times;

    String nickname;

    String gymName;

    long trainerId;

    int stamp;

    List<CouponPageTrainerList> trainerIds;
}
