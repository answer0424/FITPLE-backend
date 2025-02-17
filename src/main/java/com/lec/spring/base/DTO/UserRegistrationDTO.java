package com.lec.spring.base.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("email")
    private String email;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("birth")
    private Date birth;

    @JsonProperty("address")
    private String address;

    // Gym 정보
    @JsonProperty("gymName")
    private String gymName;

    @JsonProperty("lat") // JSON의 lat 필드를 DTO의 latitude에 매핑
    private Double latitude;

    @JsonProperty("lng") // JSON의 lng 필드를 DTO의 longitude에 매핑
    private Double longitude;

    @JsonProperty("gym")
    private GymDTO gym;

    public void setGym(GymDTO gym) {
        this.address = gym.getAddress();
        this.gymName = gym.getGymName();
        this.latitude = gym.getLatitude();
        this.longitude = gym.getLongitude();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GymDTO {
        private String gymName;
        private String address;
        private Double latitude;
        private Double longitude;
    }
}
