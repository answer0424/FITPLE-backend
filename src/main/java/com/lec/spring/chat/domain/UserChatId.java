package com.lec.spring.chat.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserChatId implements Serializable {
    @Column(name = "user_id")  // snake_case로 변경
    private Long userId;

    @Column(name = "chat_id")  // snake_case로 변경
    private Long chatId;
}
