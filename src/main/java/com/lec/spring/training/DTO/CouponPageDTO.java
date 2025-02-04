package com.lec.spring.training.DTO;

import com.lec.spring.base.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CouponPageDTO {
    private int coupons;

    private Integer times;

    private String nickname;

    List<User> trainerIds;
}
