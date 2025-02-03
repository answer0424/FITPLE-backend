package com.lec.spring.training.controller;


import com.lec.spring.training.DTO.TrainerProfileReadDTO;
import com.lec.spring.training.service.TrainerProfileDTOService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quiz/trainers")
@RequiredArgsConstructor
public class TrainerProfileController {
    private final TrainerProfileDTOService trainerProfileDTOService;

    // 특정 트레이너 프로필 조회 (DTO 반환)
    @GetMapping("/{userId}/detail")
    public ResponseEntity<TrainerProfileReadDTO> getTrainerProfile(@PathVariable Long userId) {
        TrainerProfileReadDTO trainerProfileDTO = trainerProfileDTOService.getTrainerProfileById(userId);
        return ResponseEntity.ok(trainerProfileDTO);
    }



}
