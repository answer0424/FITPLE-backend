package com.lec.spring.chat.service;

import com.lec.spring.base.domain.User;
import com.lec.spring.chat.domain.Chat;
import com.lec.spring.chat.domain.Message;
import com.lec.spring.chat.domain.UserChatId;
import com.lec.spring.chat.repository.ChatRepository;
import com.lec.spring.chat.repository.ChatRoomParticipantRepository;
import com.lec.spring.chat.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final MessageRepository messageRepository;

    public ChatService(ChatRepository chatRepository, ChatRoomParticipantRepository chatRoomParticipantRepository, MessageRepository messageRepository) {
        this.chatRepository = chatRepository;
        this.chatRoomParticipantRepository = chatRoomParticipantRepository;
        this.messageRepository = messageRepository;
    }

    // 채팅방 생성 메서드
    public Chat createChatRoom() {
        Chat chat = new Chat();
        return chatRepository.save(chat);
    }

    // 채팅방 삭제 메서드
    public boolean deleteChatRoom(Long chatId) {
        Optional<Chat> chat = chatRepository.findById(chatId);
        if (chat.isPresent()) {
            chatRepository.delete(chat.get());
            return true;
        } else {
            return false;
        }
    }

    public Message createMessage(String content, Long senderId, Long chatId) {
        UserChatId userChatId = new UserChatId();
        userChatId.setChatId(chatId);
        userChatId.setUserId(chatRoomParticipantRepository.getUserIdByUsername(senderId));

        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new IllegalArgumentException("Invalid chat room ID"));

        Message message = new Message().builder()
                .content(content)
                .chat(chat)
                .isChecked(false)
                .build();

        return messageRepository.save(message);
    }


}
