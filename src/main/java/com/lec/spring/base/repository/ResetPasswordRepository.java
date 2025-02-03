package com.lec.spring.base.repository;

import com.lec.spring.base.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetPasswordRepository extends JpaRepository<User, Long> {

    int updatePassword(Long id, String newPassword);
}//end ResetPasswordRepository
