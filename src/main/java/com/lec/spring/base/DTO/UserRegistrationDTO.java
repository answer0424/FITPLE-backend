package com.lec.spring.base.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 회원가입용
public class UserRegistrationDTO {
    private String email;
    private String username;
    private String password;
    private String nickname;
    private Date birth;
    private String address;

    // Gym 정보
    private String gymName;
    private Double latitude;
    private Double longitude;

}
