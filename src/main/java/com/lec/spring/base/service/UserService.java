package com.lec.spring.base.service;

import com.lec.spring.base.DTO.MyPageUserInfoDTO;
import com.lec.spring.base.domain.User;
import com.lec.spring.base.DTO.UserRegistrationDTO;
import com.lec.spring.base.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserRegistrationDTO registrationDTO, String role) {
        String email = registrationDTO.getEmail();
        String username = registrationDTO.getUsername();
        String password = passwordEncoder.encode(registrationDTO.getPassword());
        String nickname = registrationDTO.getNickname();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date birth = registrationDTO.getBirth();
        String formatBirth = formatter.format(birth);
        String address = registrationDTO.getAddress();

        // 회원가입 시 이미 존재하는 id 라면 회원가입 실패
        if (userRepository.existsByUsername(username)) {
            return null;
        }

        User user = User.builder()
                .email(email)
                .username(username.toUpperCase())
                .password(password)  // 이미 인코딩된 비밀번호 사용
                .nickname(nickname)
                .birth(birth)
                .address(address)
                .authority(role)  // 유저의 권한 설정
                .build();

        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username.toUpperCase());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //마이페이지 유저 정보 채우기
    public MyPageUserInfoDTO getMyPageUserInfo(long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저 탐색에 실패했습니다"));
        return MyPageUserInfoDTO.builder()
                .userId(id)
                .nickName(u.getNickname())
                .profileImage(u.getProfileImage())
                .email(u.getEmail())
                .birth(u.getBirth())
                .address(u.getAddress())
                .build();
    }

    // @value("${app.image.profile-image}")
    //String dir;
    //이미지 변경
    public boolean changeUserProfileImage(MultipartFile image, Long userId){
        //TODO 이미지 관련
        return true;
    }

    //회원 정보 변경
    public void changeUserProfile(MyPageUserInfoDTO newUserInfo){
        User user = userRepository.findById(newUserInfo.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("등록된 유저를 찾는데 실패했습니다."));
        //TODO 매우 짜치는 반복코드. MapStruct 어노테이션 고려
        if (newUserInfo.getNickName() != null) {
            user.setNickname(newUserInfo.getNickName());
        }
        if (newUserInfo.getAddress() != null) {
            user.setAddress(newUserInfo.getAddress());
        }
        if (newUserInfo.getEmail() != null) {
            user.setEmail(newUserInfo.getEmail());
        }
        if (newUserInfo.getBirth() != null) {
            user.setBirth(newUserInfo.getBirth());
        }

        userRepository.save(user);
    }

    //유저 탈퇴
    public void DeleteMember(long id) {
        userRepository.delete(userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("탈퇴할 유저 검색에 실패했습니다")));
        userRepository.flush();
    }
}
