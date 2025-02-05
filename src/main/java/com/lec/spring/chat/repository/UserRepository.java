package com.lec.spring.chat.repository;

import com.lec.spring.base.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
