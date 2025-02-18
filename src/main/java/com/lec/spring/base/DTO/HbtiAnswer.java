package com.lec.spring.base.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HbtiAnswer {
    private Long userId; // 사용자 ID
    private List<Integer> answers; // 사용자가 응답한 12개의 답변 리스트
}
