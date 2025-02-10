package com.lec.spring.chat.repository;

import com.lec.spring.base.domain.User;
import com.lec.spring.base.domain.User;
import com.lec.spring.chat.domain.Chat;
import com.lec.spring.chat.domain.UserChat;
import com.lec.spring.chat.domain.UserChatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserChatRepository extends JpaRepository<UserChat, UserChatId> {

    @Query("SELECT uc1.chat.id FROM UserChat uc1 " +
            "JOIN UserChat uc2 ON uc1.chat.id = uc2.chat.id " +
            "WHERE uc1.user.id = :userId1 AND uc2.user.id = :userId2")
    Optional<Long> findCommonChatId(@Param("userId1") Long userId1, @Param("userId2") Long userId2);


    // 채팅방에서 특정 유저 제거
    @Modifying
    @Transactional
    @Query("DELETE FROM UserChat uc WHERE uc.user.id = :userId AND uc.chat.id = :chatId")
    void deleteByUserIdAndChatId(@Param("userId") Long userId, @Param("chatId") Long chatId);

    // 특정 채팅방에 남아 있는 유저가 있는지 확인
    boolean existsByChatId(Long chatId);

    List<UserChat> findByChatId(Long chatId);

    // 채팅방 목록 조회 시 특정 유저 id 값 확인하기
    List<UserChat> findByUserId(Long userId);  // 사용자가 속한 채팅방 조회

    @Query("SELECT uc.chat.id FROM UserChat uc WHERE uc.user.id = :trainerId")
    List<Long> findChatIdsByUserId(@Param("trainerId") Long trainerId);



    // 트레이너의 채팅방에 참여한 학생 조회
    @Query("SELECT u FROM User u JOIN UserChat uc ON u.id = uc.user.id " +
            "WHERE uc.chat.id IN :chatIds AND u.authority = 'ROLE_STUDENT'")
    List<User> findStudentsInChats(@Param("chatIds") List<Long> chatIds);


    @Query("SELECT uc.user FROM UserChat uc WHERE uc.chat.id = :chatId AND uc.user.id <> :userId")
    Optional<User> findOtherUserInChat(Long chatId, Long userId);


    //상대가 나와 같은 채팅방에 속해있는지 확인.
    @Query("""
        SELECT COUNT(uc1.chat.id) > 0 
        FROM UserChat uc1 
        JOIN UserChat uc2 ON uc1.chat.id = uc2.chat.id
        WHERE uc1.user.id = :trainerId
        AND uc2.user.id = :studentId
    """)
    boolean existsCommonChatRoom(@Param("trainerId") Long myId, @Param("studentId") Long otherId);
}


