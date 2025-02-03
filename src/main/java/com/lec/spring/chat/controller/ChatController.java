package com.lec.spring.chat.controller;

import com.lec.spring.chat.domain.Chat;
import com.lec.spring.chat.domain.Message;
import com.lec.spring.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // HTTP 요청을 위한 엔드포인트
    @PostMapping("/sendMessage")
    public Message sendMessage(@RequestParam String content, @RequestParam Long senderId, @RequestParam Long chatId) {
        return chatService.createMessage(content, senderId, chatId);
    }

    @PostMapping("/createRoom")
    public Chat createChatRoom() {
        return chatService.createChatRoom();
    }

    @DeleteMapping("/deleteRoom/{chatRoomId}")
    public boolean deleteChatRoom(@PathVariable Long chatRoomId) {
        return chatService.deleteChatRoom(chatRoomId);
    }

    // WebSocket 메시지 처리를 위한 핸들러
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Message handleChatMessage(Message chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        return chatService.createMessage(chatMessage.getContent(), chatMessage.getUser().getId(), chatMessage.getChat().getId());
    }
}
