package com.lec.spring.base.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class MyPageUserInfoDTO {
    long userId;
    String nickName;
    String profileImage;
    String HBTI;
    String address;
    String email;
    Date birth;
}
