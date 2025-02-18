package com.lec.spring.chat.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomStatusDTO {
    private Long roomId;  // 채팅방 ID
    private Long userId;  // 유저 ID
    private String status; // "enter" 또는 "exit" 상태
}
