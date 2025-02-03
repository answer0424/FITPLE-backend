package com.lec.spring.base.controller;

import com.lec.spring.base.DTO.HbtiAnswer;
import com.lec.spring.base.service.HbtiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hbti")
@RequiredArgsConstructor
public class HbtiController {

    private final HbtiService hbtiService;

    /**
     * 답변을 기반으로 HBTI 결과를 계산
     */
    @PostMapping("/calculate")
    public ResponseEntity<Map<String, Object>> calculateHbti(@RequestBody List<Integer> answers) {
        try {
            Map<String, Object> result = hbtiService.calculateHbtiResult(answers);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "HBTI 데이터를 로드하는 중 문제가 발생했습니다."));
        }
    }

    /**
     * 전체 HBTI JSON 데이터를 반환
     */
    @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> getAllHbtiData() {
        try {
            Map<String, Object> hbtiData = hbtiService.getAllHbtiData();
            return ResponseEntity.ok(hbtiData);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "HBTI 데이터를 로드하는 중 문제가 발생했습니다."));
        }
    }

    /**
     * 사용자 ID와 답변을 기반으로 HBTI 결과를 저장
     */
    @PostMapping("/save")
    @Transactional
    public ResponseEntity<String> saveHbtiResult(@RequestBody HbtiAnswer request) {
        try {
            hbtiService.processHbti(request.getUserId(), request.getAnswers());
            return ResponseEntity.ok("HBTI 결과가 성공적으로 저장되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 특정 HBTI 유형의 데이터를 조회
     */
    @GetMapping("/type/{hbtiType}")
    public ResponseEntity<Map<String, Object>> getHbtiDataByType(@PathVariable String hbtiType) {
        try {
            Map<String, Object> hbtiData = hbtiService.getHbtiDataByType(hbtiType);
            return ResponseEntity.ok(hbtiData);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "HBTI 데이터를 로드하는 중 문제가 발생했습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 특정 사용자 ID로 HBTI 결과와 세부 정보를 결합하여 반환
     */
    @GetMapping("/{userId}/result")
    public ResponseEntity<Map<String, Object>> getHbtiResultWithDetailsAndMatches(@PathVariable Long userId) {
        try {
            Map<String, Object> result = hbtiService.getHbtiResultWithDetailsAndMatches(userId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "HBTI 데이터를 로드하는 중 문제가 발생했습니다."));

        }
    }

    /**
     * 특정 사용자 ID로 저장된 HBTI 결과를 조회
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getHbtiResult(@PathVariable Long userId) {
        try {
            Map<String, Object> result = hbtiService.getHbtiResultWithDetails(userId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "HBTI 데이터를 로드하는 중 문제가 발생했습니다."));
        }
    }
}
