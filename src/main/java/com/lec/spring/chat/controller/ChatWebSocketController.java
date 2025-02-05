package com.lec.spring.chat.controller;

import com.lec.spring.chat.DTO.MessageDTO;
import com.lec.spring.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{chatId}/send")
    public void sendMessage(@DestinationVariable Long chatId, MessageDTO messageDTO) {
        System.out.println(chatId + "인 방에 " + messageDTO.getContent() + "를 보냈습니다.");
        MessageDTO message = chatService.saveMessage(chatId, messageDTO);

        // 클라이언트에게 메시지 전송
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, message);

        sendNotification(chatId, messageDTO);
    }

    // 메시지 전송 시 알림 전송
    private void sendNotification(Long chatId, MessageDTO messageDTO) {
        String notification = "New message in chat " + chatId + " : " + messageDTO.getContent();

        messagingTemplate.convertAndSend("/topic/notifications" , notification);
    }
}

