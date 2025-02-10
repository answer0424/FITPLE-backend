package com.lec.spring.chat.DTO;

import com.lec.spring.chat.domain.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {
    private Long messageId;
    private Long userId;
    private String content;
    private boolean isChecked;
    private LocalDateTime createdAt;
    private Long chatId;

    public static MessageDTO fromEntity(Message message) {
        return MessageDTO.builder()
                .messageId(message.getMessageId())
                .userId(message.getUser().getId())
                .content(message.getContent())
                .isChecked(message.isChecked())
                .createdAt(message.getCreatedAt())
                .chatId(message.getChat().getId())
                .build();
    }
}

