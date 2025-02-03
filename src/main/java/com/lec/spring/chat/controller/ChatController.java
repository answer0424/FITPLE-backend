package com.lec.spring.chat.controller;

import com.lec.spring.chat.service.ChatMessageCreateUseCase;
import com.lec.spring.chat.command.ChatMessageCreateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
class ChatController {
    private final ChatMessageCreateUseCase chatMessageCreateUseCase;
    @MessageMapping("/chat/rooms/{roomId}/send")
    @SendTo("/topic/public/rooms/{roomId}")
    public ChatMessageResponse sendMessage(@DestinationVariable Long roomId, @Payload ChatMessageRequest chatMessage) {
        ChatMessageCreateCommand chatMessageCreateCommand = ChatMessageCreateCommand.builder()
                .content(chatMessage.text())
                .from(chatMessage.from())
                .roomId(roomId)
                .build();
        Long chatId = chatMessageCreateUseCase.createChatMessage(chatMessageCreateCommand); // DB에 등록 후 Chat Message Id 반환
        ChatMessageResponse chatMessageResponse = ChatMessageResponse.builder()
                .id(chatId)
                .content(chatMessage.text())
                .writer(chatMessage.from())
                .build();
        return chatMessageResponse;
    }
}
