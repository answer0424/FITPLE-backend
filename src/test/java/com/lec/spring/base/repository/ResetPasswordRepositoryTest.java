package com.lec.spring.base.repository;

import com.lec.spring.base.domain.EmailMessage;
import com.lec.spring.base.domain.User;
import com.lec.spring.base.service.ResetPasswordServiceImpl;
import com.lec.spring.base.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest

class ResetPasswordRepositoryTest {

    @Autowired
    ResetPasswordServiceImpl resetPasswordService;

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;


    @Test
    void test1() {

        // 임시로 User 객체 생성
        User user = new User();
        user.setUsername("admin");
        user.setNickname("지윤");
        user.setPassword("123456"); // 실제 환경에서는 암호화된 비밀번호로 저장하는 것이 좋습니다
        user.setEmail("cooconut0226@gmail.com");

        // 저장
        userRepository.save(user);
        System.out.println("회원이 저장되었습니다: " + user.getUsername());
        // 이메일 메시지 생성
        EmailMessage emailMessage = EmailMessage.builder()
                .to("cooconut0226@gmail.com")
                .subject("hihi")
                .build();

        System.out.println("########emailMessage : " + emailMessage);

        // 이메일 전송
        String result = resetPasswordService.sendEmail(emailMessage);
        System.out.println("이메일 전송 결과: " + result);
    }
}
