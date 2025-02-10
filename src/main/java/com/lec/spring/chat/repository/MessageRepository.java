package com.lec.spring.chat.repository;

import com.lec.spring.chat.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // 특정 채팅방의 메시지를 시간 순으로 가져오기
    List<Message> findByChatIdOrderByTimestampAsc(Long chatId);

    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId AND m.user.id != :userId AND m.isChecked = false")
    List<Message> findUnreadMessages(@Param("chatId") Long chatId, @Param("userId") Long userId);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Message m SET m.isChecked = true WHERE m.chat.id = :chatId AND m.user.id != :userId AND m.isChecked = false")
    void markMessagesAsRead(@Param("chatId") Long chatId, @Param("userId") Long userId);

}
