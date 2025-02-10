package com.lec.spring.chat.DTO;

import com.lec.spring.base.domain.User;
import com.lec.spring.chat.domain.Chat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatDTO {
    private Long chatId;
    private Long otherUserId;
    private String otherNickname;
    private String otherProfileImage;

    public ChatDTO(Chat chat, User otherUser) {
        this.chatId = chat.getId();
        if (otherUser != null) {
            this.otherUserId = otherUser.getId();
            this.otherNickname = otherUser.getNickname();
            this.otherProfileImage = otherUser.getProfileImage();
        }
    }
}

