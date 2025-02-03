package com.lec.spring.chat.repository;

import com.lec.spring.chat.domain.UserChatId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomParticipantRepository extends JpaRepository<UserChatId, Long> {
}
