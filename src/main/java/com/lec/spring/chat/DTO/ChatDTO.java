package com.lec.spring.chat.DTO;

import com.lec.spring.chat.domain.Chat;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ChatDTO {
    private Long chatId;


    public ChatDTO(Chat chat) {
        this.chatId = chat.getId();
    }
}
