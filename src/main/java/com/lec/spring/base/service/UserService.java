package com.lec.spring.base.service;

import com.lec.spring.base.DTO.MyPageUserInfoDTO;
import com.lec.spring.base.DTO.TrainerInfoDTO;
import com.lec.spring.base.domain.Gym;
import com.lec.spring.base.domain.HBTI;
import com.lec.spring.base.domain.User;
import com.lec.spring.base.DTO.UserRegistrationDTO;
import com.lec.spring.base.repository.GymRepository;
import com.lec.spring.base.repository.HbtiRepository;
import com.lec.spring.base.repository.UserRepository;
import com.lec.spring.base.service.mapper.UserMapper;
import com.lec.spring.training.domain.Training;
import com.lec.spring.training.repository.TrainingRepository;
import com.lec.spring.training.service.ImgServiceImpl;
import com.lec.spring.training.service.MyPageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GymRepository gymRepository;
    private final ImgServiceImpl imgService;
    private final UserMapper userMapper;
    private final HbtiRepository hbtiRepository;
    private final TrainingRepository trainingRepository;
    private final MyPageService myPageService;

    @Value("${app.image.profile}")
    String dir;

    public User registerUser(UserRegistrationDTO registrationDTO, String role) {
        String email = registrationDTO.getEmail();
        String username = registrationDTO.getUsername();
        String password = passwordEncoder.encode(registrationDTO.getPassword());
        String nickname = registrationDTO.getNickname();
        Date birth = registrationDTO.getBirth();
        String address = registrationDTO.getAddress();
        String gymName = registrationDTO.getGymName();
        Double latitude = registrationDTO.getLatitude();
        Double longitude = registrationDTO.getLongitude();

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

        // 유저 정보를 우선적으로 저장
        user = userRepository.save(user);

        // trainer 회원가입 시 GYM 정보도 같이 저장
        if("ROLE_TRAINER".equals(role)) {
            Gym gym = gymRepository.findByAddress(address);
            if(gym == null) {
                gym = Gym.builder()
                        .name(gymName)
                        .address(address)
                        .latitude(latitude)
                        .longitude(longitude)
                        .build();
                gymRepository.save(gym);
            }

            user.setGym(gym);   // gymId 저장
            user = userRepository.save(user);
        }

       return user;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username.toUpperCase());
    }


    //마이페이지 유저 정보 채우기
    public MyPageUserInfoDTO getMyPageUserInfo(long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("유저 탐색에 실패했습니다"));

        MyPageUserInfoDTO uInfo = userMapper.toDto(u);

        Optional<HBTI> h = hbtiRepository.findById(id);

        if(!h.isPresent()) {
            uInfo.setHBTI(null);
        } else {
            uInfo.setHBTI(h.orElseThrow().getHbti());
        }

        return uInfo;

    }


    //유저 프로필 이미지 변경
    @Transactional(rollbackOn = Exception.class)
    public boolean changeUserProfileImage(MultipartFile image, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저 검색에 실패했습니다."));
        try {
            String ddir = imgService.saveImage(image, dir);
            user.setProfileImage(ddir);
            userRepository.saveAndFlush(user);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 중 오류 발생");
        }
    }

    //회원 정보 변경
    public void changeUserProfile(MyPageUserInfoDTO newUserInfo){
        User user = userRepository.findById(newUserInfo.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("등록된 유저를 찾는데 실패했습니다."));
        //매우 짜치는 반복코드. MapStruct 어노테이션 고려
//        if (newUserInfo.getNickname() != null) {
//            user.setNickname(newUserInfo.getNickname());}
//        if (newUserInfo.getAddress() != null) {
//            user.setAddress(newUserInfo.getAddress());}
//        if (newUserInfo.getEmail() != null) {
//            user.setEmail(newUserInfo.getEmail());}
//        if (newUserInfo.getBirth() != null) {
//            user.setBirth(newUserInfo.getBirth());}
        userMapper.UserFromMyPageUserInfoDto(newUserInfo, user);

        userRepository.saveAndFlush(user);

        // trainer 회원가입 시 GYM 정보도 같이 저장
        if("ROLE_TRAINER".equals(user.getAuthority())) {
            Gym gym = gymRepository.findByAddress(newUserInfo.getAddress());
            if (gym == null) {
                gym = Gym.builder()
                        .name(newUserInfo.getGym().getName())
                        .address(newUserInfo.getGym().getAddress())
                        .latitude(newUserInfo.getGym().getLatitude())
                        .longitude(newUserInfo.getGym().getLongitude())
                        .build();
                gymRepository.save(gym);
            }

            user.setGym(gym);   // gymId 저장
            user = userRepository.saveAndFlush(user);
        }
    }

    //유저 탈퇴
    public void deleteUser(long id) {
        userRepository.delete(userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("탈퇴할 유저 검색에 실패했습니다")));
        userRepository.flush();
    }

    public Page<User> getAllTrainers(Pageable pageable) {
        return userRepository.findByAuthority("ROLE_TRAINER", pageable);
    }

    public Page<User> getAllStudents(Pageable pageable) {
        return userRepository.findByAuthority("ROLE_STUDENT", pageable);
    }

    // adminpage
    public List<TrainerInfoDTO> getTrainerInfoForStudent(Long studentId) {
        List<Training> trainings = trainingRepository.findByUserId(studentId);
        return trainings.stream()
                .map(training -> TrainerInfoDTO.builder()
                        .trainerId(training.getTrainer().getId())
                        .email(training.getTrainer().getEmail())
                        .name(training.getTrainer().getNickname())
                        .remainingSessions(myPageService.getPtCount(studentId, training.getTrainer().getId()))
                        .build())
                .collect(Collectors.toList());
    }


    @Transactional
    public void deleteTrainer(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!"ROLE_TRAINER".equals(user.getAuthority())) {
            throw new IllegalArgumentException("User is not a trainer");
        }

        userRepository.delete(user);
    }

    @Transactional
    public void deleteStudent(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!"ROLE_STUDENT".equals(user.getAuthority())) {
            throw new IllegalArgumentException("User is not a student");
        }

        userRepository.delete(user);
    }
}


