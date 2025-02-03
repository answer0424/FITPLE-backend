package com.lec.spring.base.service;

import com.lec.spring.base.domain.EmailMessage;
import com.lec.spring.base.domain.User;
import com.lec.spring.base.repository.ResetPasswordRepository;
import com.lec.spring.base.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.naming.Context;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class ResetPasswordServiceImpl implements ResetPasswordService {
    private final RedisTemplate redisTemplate;
    private JavaMailSender mailSender;
    private ResetPasswordRepository resetPasswordRepository;
    private final EmailAuthService emailAuthService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static int number;


    public ResetPasswordServiceImpl(RedisTemplate redisTemplate, EmailAuthService emailAuthService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.redisTemplate = redisTemplate;
        this.emailAuthService = emailAuthService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    // # 랜덤 번호 생성
    @Override
    public void createNumber() {

        number = (int) (Math.random() * 90000) + 100000;
    }
    // user에 이메일로 재설정 링크 보내는 동작
    @Override
    public MimeMessage createEmail(EmailMessage email) {
        createNumber(); // 인증번호 생성
        emailAuthService.storeAuthCode(email.getTo(), String.valueOf(number));
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
            mimeMessageHelper.setTo(email.getTo());
            mimeMessageHelper.setSubject(email.getSubject());
            mimeMessageHelper.setText(email.getMessage(), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    return message;
    }

    @Override
    public boolean isExistEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
        }

    @Override
    public String sendEmail(EmailMessage emailMessage) {
        // 이메일로 회원 정보 조회
        User user = userRepository.findByEmail(emailMessage.getTo());
        if (user == null) {
            System.out.println("이메일이 등록되지 않았습니다.");
            return "이메일이 등록되지 않았습니다.";
        } else {
            System.out.println(user + "에게 이메일이 전송되었습니다.");
        }

        // UUID 생성 및 Redis에 저장 (3분 TTL)
        String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(uuid, user.getUsername(), 3, TimeUnit.MINUTES);

        // 동적 URL 생성
        String resetLink = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/member/reset-password")
                .queryParam("id", user.getUsername())
                .queryParam("uuid", uuid)
                .toUriString();

        // 이메일 내용 작성 (HTML 형식, React에서 처리하기 용이한 링크 포함)
        String emailContent = "<html>" +
                "<body>" +
                "<h2>비밀번호 재설정</h2>" +
                "<p>안녕하세요, " + user.getUsername() + "님.</p>" +
                "<p>아래 링크를 클릭하여 비밀번호를 재설정해주세요:</p>" +
                "<a href=\"" + resetLink + "\">비밀번호 재설정</a>" +
                "</body>" +
                "</html>";

        // 이메일 전송
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailMessage.getTo());
            mimeMessageHelper.setSubject(emailMessage.getSubject());
            mimeMessageHelper.setText(emailContent, true); // HTML 형식으로 이메일 전송
            mailSender.send(mimeMessage);
            return "success";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "이메일 전송 중 오류가 발생했습니다.";
        }
    }


    @Override
    public boolean updatePassword(Long id, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        System.out.println("#########encodedPassword: " + encodedPassword);
        Map<String, String> updatelist = new HashMap<String, String>();
        updatelist.put(String.valueOf(id), "id");
        updatelist.put("newPassword", encodedPassword);
        int result = resetPasswordRepository.updatePassword(id, encodedPassword);
        System.out.println("update : " +result);
        if (result == 1) {
            return true;
        }
        return false;
    }




    //인증 유효성
    @Override
    public boolean verifyAuthorizationCode(String code, String email) {
        String savedEmail = (String) redisTemplate.opsForValue().get(code);
        return savedEmail != null && savedEmail.equals(email);
    }


}// end ResetPasswordServiceImple
