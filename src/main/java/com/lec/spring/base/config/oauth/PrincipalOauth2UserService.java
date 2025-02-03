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

        System.out.println("""
                [loadUser() 호출]
                  ClientRegistration: %s
                  RegistratoinId: %s
                  AccessToken: %s
                  OAuth2User Attributes: %s
                """.formatted(userRequest.getClientRegistration()  // ClientRegistration
                , userRequest.getClientRegistration().getRegistrationId()  // String. 어떤 OAuth로 로그인 했는지 알수 있다.
                , userRequest.getAccessToken().getTokenValue() // String
                , oAuth2User.getAttributes()  // Map<String, Object> ← 사용자 프로필 정보가 있다.
        ));

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
            email = "fitple@domain.com";  // 이메일이 없을 때 기본값 설정
        }
        String nickname = provider + "_" + email;


        System.out.println("""
                [OAuth2인증 회원 정보]
                  username: %s
                  nickname: %s
                  email: %s
                  password: %s
                  provider: %s
                  providerId: %s
                """.formatted(username, nickname, email, password, provider, providerId));

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
                System.out.println("\noauth2 인증 회원 가입 성공\n");
                user = userRepository.findByUsername(username);
            } else {
                System.out.println("\noauth2 인증 회원 가입 실패 !\n");
            }
        } else {
            System.out.println("\n이미 가입된 회원입니다.\n");
        }

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }
}
