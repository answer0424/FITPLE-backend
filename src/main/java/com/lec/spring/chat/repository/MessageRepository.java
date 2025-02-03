package com.lec.spring.chat.repository;

import com.lec.spring.chat.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // 특정 채팅방의 메시지를 시간 순으로 가져오기
    List<Message> findByChatIdOrderByTimestampAsc(Long chatId);
}
