package com.lec.spring.base.config.oauth;

import com.lec.spring.base.DTO.UserRegistrationDTO;
import com.lec.spring.base.config.PrincipalDetails;
import com.lec.spring.base.config.oauth.provider.GoogleUserInfo;
import com.lec.spring.base.config.oauth.provider.KakaoUserInfo;
import com.lec.spring.base.config.oauth.provider.NaverUserInfo;
import com.lec.spring.base.config.oauth.provider.OAuth2UserInfo;
import com.lec.spring.base.domain.User;
import com.lec.spring.base.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public PrincipalOauth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Value("${app.oauth2.password}")
    private String oauth2Password;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 사용자 프로필 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);


        // 강제 회원가입 진행
        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = switch (provider.toLowerCase()) {
            case "google" -> new GoogleUserInfo(oAuth2User.getAttributes());
            case "naver" -> new NaverUserInfo(oAuth2User.getAttributes());
            case "kakao" -> new KakaoUserInfo(oAuth2User.getAttributes());
            default -> null;
        };

        if (oAuth2UserInfo == null) {
            throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
        }

        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId;
        String password = oauth2Password;
        String email = oAuth2UserInfo.getEmail();
        if (email == null) {
            email = "fitple" + providerId + "@domain.com";  // 이메일이 없을 때 기본값 설정
        }
        String nickname = username;



        // 회원가입 전 이미 가입된 회원인지 확인
        User user = userRepository.findByUsername(username);

        // 신규 회원가입
        if (user == null) {
            User newUser = User.builder()
                    .email(email)
                    .username(username.toUpperCase())
                    .password(password)
                    .nickname(nickname)
                    .authority("ROLE_STUDENT")
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            newUser = userRepository.save(newUser);

            if (newUser != null) {
                user = userRepository.findByUsername(username);
            }
        }

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }
}
