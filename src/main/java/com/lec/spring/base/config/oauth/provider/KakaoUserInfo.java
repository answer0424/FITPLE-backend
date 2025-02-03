package com.lec.spring.base.config.oauth.provider;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {
    private Map<String, Object> attributes;
    private Map<String, Object> kakaoAccountAttributes;
    private Map<String, Object> profileAttributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccountAttributes = (Map<String, Object>) attributes.get("kakao_account");
        this.profileAttributes = (Map<String, Object>) kakaoAccountAttributes.get("profile");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();  // kakao 는 id가 숫자값인데 이 경우 Long 타입이 리턴되니까 toString() 으로 리턴해준다.
    }

    @Override
    public String getEmail() {
        return null;
    }

//    @Override
//    public String getEmail() {
//        return (String) kakaoAccountAttributes.get("email");  // 이메일을 kakao_account에서 가져오기
//    }

    @Override
    public String getName() {
        // kakao_account.profile.nickname 값
        return (String) profileAttributes.get("nickname");
    }
}
