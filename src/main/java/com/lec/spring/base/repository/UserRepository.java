package com.lec.spring.base.repository;


import com.lec.spring.base.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // 회원 가입 시 필요
    Boolean existsByUsername(String username);

    User findByUsername(String username);

    User findByEmail(String email);
}
