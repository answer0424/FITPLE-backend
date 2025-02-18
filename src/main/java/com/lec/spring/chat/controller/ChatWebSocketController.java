package com.lec.spring.chat.controller;

import com.lec.spring.chat.DTO.ChatRoomStatusDTO;
import com.lec.spring.chat.DTO.MessageDTO;
import com.lec.spring.chat.DTO.ReadReceiptDTO;
import com.lec.spring.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // 현재 접속 중인 유저를 저장하는 Map (roomId -> Set<UserId>)
    private final Map<Long, Set<Long>> activeUsers = new HashMap<>();


    @MessageMapping("/chat/{chatId}/send")
    public void sendMessage(@DestinationVariable Long chatId, MessageDTO messageDTO) {
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

    // 유저가 채팅방에 입장했을 때
    @MessageMapping("/chat/enter")
    public void enterChatRoom(ChatRoomStatusDTO status) {
        activeUsers.computeIfAbsent(status.getRoomId(), k -> new HashSet<>()).add(status.getUserId());

    }

    // 유저가 채팅방에 나갔을 때
    @MessageMapping("/chat/exit")
    public void exitChatRoom(ChatRoomStatusDTO status) {
        Set<Long> users = activeUsers.getOrDefault(status.getRoomId(), new HashSet<>());
        users.remove(status.getUserId());

        if(users.isEmpty()) {
            activeUsers.remove(status.getRoomId());
        }

    }

    // 메시지 읽음 처리
    @MessageMapping("/chat/read")
    public void readChatRoom(ChatRoomStatusDTO status) {

        // 메시지를 읽음 처리
        // DB에서 해당 메시지를 읽음 처리하는 로직 호출
        List<Long> readMessages = chatService.updateChatIsChecked(status.getRoomId(), status.getUserId());

        // 읽음 상태를 모든 유저에게 전송
        messagingTemplate.convertAndSend("/topic/chat/read/" + status.getRoomId(),
                new ReadReceiptDTO(status.getRoomId(), status.getUserId(), readMessages));
    }
}

