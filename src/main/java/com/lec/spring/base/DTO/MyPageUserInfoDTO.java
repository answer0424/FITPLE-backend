package com.lec.spring.base.DTO;

import com.lec.spring.base.domain.Gym;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyPageUserInfoDTO {
    long userId;
    Gym gym;
    String authority;
    String nickname;
    String profileImage;
    String HBTI;
    String address;
    String email;
    Date birth;
}
