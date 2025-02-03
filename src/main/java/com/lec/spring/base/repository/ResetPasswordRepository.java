package com.lec.spring.base.repository;

import com.lec.spring.base.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ResetPasswordRepository extends JpaRepository<User, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.id = :id")
    int updatePassword(Long id, String newPassword);

}//end ResetPasswordRepository
