package com.lec.spring.chat.repository;

import com.lec.spring.chat.domain.UserChat;
import com.lec.spring.chat.domain.UserChatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserChatRepository extends JpaRepository<UserChat, UserChatId> {

    // 특정 유저가 속한 채팅방 찾기
    @Query("SELECT uc FROM UserChat uc WHERE uc.user.id = :userId AND uc.chat.id = :chatId")
    Optional<UserChat> findByUserIdAndChatId(@Param("userId") Long userId, @Param("chatId") Long chatId);

    // 두 사용자가 공유하는 채팅방 찾기 (1:1 채팅이므로 존재하면 1개)
    @Query("SELECT uc FROM UserChat uc WHERE uc.user.id IN (:userId1, :userId2) " +
            "GROUP BY uc.chat.id, uc.user.id HAVING COUNT(DISTINCT uc.user.id) = 2")
    Optional<UserChat> findByUserIds(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT uc.chat.id FROM UserChat uc WHERE uc.user.id = :userId1 AND uc.chat.id IN (SELECT uc2.chat.id FROM UserChat uc2 WHERE uc2.user.id = :userId2)")
    Optional<Long> findCommonChatId(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // 채팅방에서 특정 유저 제거
    @Modifying
    @Transactional
    @Query("DELETE FROM UserChat uc WHERE uc.user.id = :userId AND uc.chat.id = :chatId")
    void deleteByUserIdAndChatId(@Param("userId") Long userId, @Param("chatId") Long chatId);

    // 특정 채팅방에 남아 있는 유저가 있는지 확인
    boolean existsByChatId(Long chatId);
}

