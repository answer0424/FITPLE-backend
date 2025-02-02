package com.lec.spring.base.service;

public interface EmailAuthService {
    // stored Redis
    void storeAuthCode(String email, String authCode);
    String getAuthCode(String email);

    void deletedAuthCode(String email);
}// end EmailAuthService
