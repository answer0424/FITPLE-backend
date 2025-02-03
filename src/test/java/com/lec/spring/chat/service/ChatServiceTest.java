package com.lec.spring.chat.service;

import com.lec.spring.base.domain.User;
import com.lec.spring.chat.domain.Chat;
import com.lec.spring.chat.domain.UserChat;
import com.lec.spring.chat.domain.UserChatId;
import com.lec.spring.chat.repository.ChatRepository;
import com.lec.spring.chat.repository.MessageRepository;
import com.lec.spring.chat.repository.UserChatRepository;
import com.lec.spring.chat.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class ChatServiceTest {

    private ChatRepository chatRepository;
    private UserChatRepository userChatRepository;
    private UserRepository userRepository;
    private MessageRepository messageRepository;

    @Autowired
    public ChatServiceTest(ChatRepository chatRepository, UserChatRepository userChatRepository, UserRepository userRepository, MessageRepository messageRepository) {
        this.chatRepository = chatRepository;
        this.userChatRepository = userChatRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    // 1:1 채팅방 생성 또는 기존 채팅방 반환
    @Test
    void createOrGetChat() {
        System.out.println("createOrGetChat() 호출");
        // 🔍 기존 채팅방이 있는지 확인
        Long userId = 1L;
        Long trainerId = 51L;
        Optional<UserChat> existingChat = userChatRepository.findByUserIds(userId, trainerId);
        if (existingChat.isPresent()) {
            System.out.println("이미 존재하는 채팅방");
            System.out.println(existingChat.get().getChat());
        }

        // 🔍 유저와 트레이너 정보 조회
        System.out.println("유저와 트레이너 정보 조회");
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID"));
        User trainer = userRepository.findById(trainerId).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 트레이너 ID"));

        // 🔥 새로운 채팅방 생성 (ID 먼저 생성해야 함)
        System.out.println("채팅방 생성");

        Chat chat = chatRepository.save(new Chat());
        System.out.println("생성된 채팅방 " + chat);

        // 🔥 UserChatId를 명확히 설정
        UserChatId userChatId1 = new UserChatId(userId, chat.getId());
        UserChatId userChatId2 = new UserChatId(trainerId, chat.getId());
        System.out.println("저장된 유저 정보" + userChatId1);
        System.out.println("저장된 트레이너 정보" + userChatId2);



        // 🔥 두 개의 레코드(UserChat) 삽입
        userChatRepository.save(new UserChat(userChatId1, user, chat));
        userChatRepository.save(new UserChat(userChatId2, trainer, chat));

        System.out.println(userChatRepository.findByUserIds(userId, trainerId));
    }

    // 채팅방 나가기
    @Test
    void leaveChat() {
    }

    // 해당 채팅방의 모든 사용자에게 전송할 데이터를 반환하는 메서드
    @Test
    void saveMessage() {
    }
}