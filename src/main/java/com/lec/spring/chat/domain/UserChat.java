package com.lec.spring.chat.domain;

import com.lec.spring.base.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserChat {
    @EmbeddedId
    private UserChatId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)  // snake_case로 변경
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", insertable = false, updatable = false)  // snake_case로 변경
    private Chat chat;
}
