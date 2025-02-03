package com.lec.spring.chat.controller;

import com.lec.spring.chat.domain.Chat;
import com.lec.spring.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 채팅방 생성(이미 존재한다면 재사용)
    @PostMapping("/create")
    public ResponseEntity<Chat> createChat(@RequestParam(name = "userId") Long userId,@RequestParam(name = "trainerId") Long trainerId) {
        System.out.println(userId + "와 " + trainerId + "간의 채팅방이 생성되었습니다.");
        return ResponseEntity.ok(chatService.createOrGetChat(userId, trainerId));
    }

    // 채팅방 나가기
    @DeleteMapping("/{chatId}/leave/{userId}")
    public ResponseEntity<Void> leaveChat(@PathVariable Long chatId, @PathVariable Long userId) {
        System.out.println(userId + "가" + chatId + "을 떠났습니다.");
        chatService.leaveChat(chatId, userId);
        return ResponseEntity.ok().build();
    }
}
