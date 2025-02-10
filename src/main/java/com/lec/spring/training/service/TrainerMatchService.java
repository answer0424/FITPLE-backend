package com.lec.spring.training.service;

import com.lec.spring.base.domain.HBTI;
import com.lec.spring.base.domain.User;
import com.lec.spring.base.repository.HbtiRepository;
import com.lec.spring.base.repository.UserRepository;
import com.lec.spring.training.DTO.TrainerMatchResponseDTO;
import com.lec.spring.training.domain.TrainerProfile;
import com.lec.spring.training.repository.TrainerProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TrainerMatchService {

    private final UserRepository userRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final HbtiRepository hbtiRepository;

    public List<TrainerMatchResponseDTO> findTrainersByDistrictAndHbtiTypes(Long userId, List<String> hbtiTypes, List<Map<String, Object>> topMatches) {
        // 사용자의 구 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        String userDistrict = extractDistrict(user.getAddress());

        // 해당 구의 매칭되는 HBTI를 가진 트레이너들 조회
        return trainerProfileRepository.findMatchingTrainersWithFetch(userDistrict, hbtiTypes)
                .stream()
                .map(trainer -> {
                    // 해당 트레이너의 HBTI 타입에 맞는 매칭 점수 찾기
                    Integer matchScore = topMatches.stream()
                            .filter(match -> match.get("hbtiType").equals(
                                    hbtiRepository.findByUserId(trainer.getTrainer().getId())
                                            .map(HBTI::getHbti)
                                            .orElse(null)
                            ))
                            .map(match -> (Integer) match.get("score"))
                            .findFirst()
                            .orElse(0);

                    return convertToDTO(trainer, matchScore);
                })
                .collect(Collectors.toList());
    }

    private TrainerMatchResponseDTO convertToDTO(TrainerProfile trainerProfile, Integer matchScore) {
        User trainer = trainerProfile.getTrainer();
        HBTI trainerHBTI = hbtiRepository.findByUserId(trainer.getId())
                .orElse(null);

        return TrainerMatchResponseDTO.builder()
                .trainerId(trainer.getId())
                .trainerName(trainer.getUsername())
                .nickname(trainer.getNickname())
                .hbti(trainerHBTI != null ? trainerHBTI.getHbti() : null)
                .gymName(trainer.getGym().getName())
                .profileImage(trainer.getProfileImage())
                .matchScore(matchScore)  // 매칭 점수 추가
                .build();
    }
    private String extractDistrict(String address) {
        if (address == null) return "";
        return Arrays.stream(address.split(" "))
                .filter(part -> part.endsWith("구"))
                .findFirst()
                .orElse("");
    }
}