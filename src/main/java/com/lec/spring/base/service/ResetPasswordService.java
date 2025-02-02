package com.lec.spring.base.service;

import com.lec.spring.base.domain.EmailMessage;
import com.lec.spring.base.domain.User;
import jakarta.mail.internet.MimeMessage;

public interface ResetPasswordService {


    // #이메일이 존재하는가
    boolean isExistEmail(String email);

    // #이메일 전송
    String sendEmail(EmailMessage emailMessage);

    // #update
    boolean updatePassword(Long id, String newPassword);

    // mail content
    MimeMessage createEmail (EmailMessage emailMessage);

    //랜덤 번호 생성
    void createNumber();

    // 인증코드 확인
    boolean verifyAuthorizationCode(String code, String email);


}// end ResetPasswordService
