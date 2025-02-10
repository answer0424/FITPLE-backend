package com.lec.spring.chat.controller;

import com.lec.spring.base.config.SecurityConfig;
import com.lec.spring.base.domain.User;
import com.lec.spring.chat.DTO.ChatDTO;
import com.lec.spring.chat.DTO.MessageDTO;
import com.lec.spring.chat.domain.Chat;
import com.lec.spring.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/rooms/{userId}")
    public ResponseEntity<List<ChatDTO>> getUserChats(@PathVariable Long userId) {
        System.out.println("채팅방 목록을 불러옴");

        // 사용자가 참여한 채팅방 목록 가져오기
        List<Chat> chats = chatService.getUserChats(userId);

        // 각 채팅방에서 상대방 정보 조회
        List<ChatDTO> chatDTOs = chats.stream().map(chat -> {
            // 현재 채팅방에 속한 상대방 유저 찾기 (1:1 채팅이므로 두 명만 존재)
            User otherUser = chatService.getOtherUserInChat(chat.getId(), userId);

            // ChatDTO에 상대방 정보 포함하여 반환
            return new ChatDTO(chat, otherUser);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(chatDTOs);
    }

    // 특정 채팅방의 메시지 목록 불러오기
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<MessageDTO>> getChatMessages(
            @PathVariable Long chatId,
            @RequestParam Long userId // 로그인된 사용자 ID를 프론트에서 전달
    ) {
        System.out.println(chatId + "방의 메시지가 읽음 처리됨 (요청한 사용자: " + userId + ")");

        // 로그인한 사용자가 보낸 메시지는 읽음 처리 X
        chatService.updateChatIsChecked(chatId, userId);

        List<MessageDTO> messages = chatService.getChatMessages(chatId);
        return ResponseEntity.ok(messages);
    }

}
