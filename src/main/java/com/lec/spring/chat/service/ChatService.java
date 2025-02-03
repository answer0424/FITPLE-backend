package com.lec.spring.chat.service;

import com.lec.spring.chat.domain.Chat;
import com.lec.spring.chat.repository.ChatRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatService {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
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


}
