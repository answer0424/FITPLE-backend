package com.lec.spring.chat.repository;

import com.lec.spring.base.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatUserRepository extends JpaRepository<User, Long> {
}
