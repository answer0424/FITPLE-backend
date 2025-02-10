package com.lec.spring.chat.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadReceiptDTO {
    private Long roomId;          // 채팅방 ID
    private Long userId;          // 읽은 유저 ID
    private List<Long> readMessageIds; // 읽음 처리된 메시지 ID 리스트
}
