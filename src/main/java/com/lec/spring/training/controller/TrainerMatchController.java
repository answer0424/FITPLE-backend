package com.lec.spring.training.controller;

import com.lec.spring.training.DTO.TrainerMatchResponseDTO;
import com.lec.spring.training.service.TrainerMatchService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@Slf4j
public class TrainerMatchController {

    private final TrainerMatchService trainerMatchService;

    @GetMapping("/{userId}/result/match")
    public ResponseEntity<?> getMatchingTrainers(
            @PathVariable Long userId,
            @RequestParam String hbti  // 쉼표로 구분된 HBTI 문자열로 받음
    ) {
        try {
            List<String> hbtiList = Arrays.asList(hbti.split(","));
            List<TrainerMatchResponseDTO> matchingTrainers =
                    trainerMatchService.findMatchingTrainers(userId, hbtiList);

            if (matchingTrainers.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(matchingTrainers);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error finding matching trainers: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to find matching trainers"));
        }
    }
}