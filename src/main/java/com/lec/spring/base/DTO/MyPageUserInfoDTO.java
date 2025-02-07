package com.lec.spring.base.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class MyPageUserInfoDTO {
    long userId;
    long gymId;
    String authority;
    String nickname;
    String profileImage;
    String HBTI;
    String address;
    String email;
    Date birth;
}
