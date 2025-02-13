package com.lec.spring.training.controller;

import com.lec.spring.base.domain.User;
import com.lec.spring.base.repository.UserRepository;
import com.lec.spring.base.service.HbtiMatcher;
import com.lec.spring.base.service.HbtiService;
import com.lec.spring.base.service.UserService;
import com.lec.spring.training.DTO.TrainerMatchResponseDTO;
import com.lec.spring.training.service.TrainerMatchService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@Slf4j
public class TrainerMatchController {
    private final UserRepository userRepository;
    private final TrainerMatchService trainerMatchService;
    private final HbtiMatcher hbtiMatcher;

    @GetMapping("/{userId}/result/match")
    public ResponseEntity<?> getMatchingTrainers(@PathVariable Long userId) {
        try {
            // 1. 먼저 HBTI 매칭 결과를 가져옴
            Map<String, Object> matchResult = hbtiMatcher.findTopMatches(userId);
            List<Map<String, Object>> topMatches = (List<Map<String, Object>>) matchResult.get("topMatches");

            // 2. 매칭된 HBTI 타입들 추출
            List<String> matchingHbtiTypes = topMatches.stream()
                    .map(match -> (String) match.get("hbtiType"))
                    .toList();

            // 3. 해당 HBTI 타입을 가진 트레이너들 중 같은 구에 있는 트레이너 찾기
            List<TrainerMatchResponseDTO> matchingTrainers =
                    trainerMatchService.findTrainersByDistrictAndHbtiTypes(userId, matchingHbtiTypes, topMatches);

            if (matchingTrainers.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            log.info("매칭된 트레이너 수: {}", matchingTrainers.size());
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

    //
    @GetMapping("/search")
    public ResponseEntity<?> getTrainers() {
        List<User> trainer = userRepository.findByAuthorities( "ROLE_TRAINER");
        return ResponseEntity.ok(trainer);
    }

}