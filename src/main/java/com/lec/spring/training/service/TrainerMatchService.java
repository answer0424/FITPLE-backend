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
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TrainerMatchService {

    private final UserRepository userRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final HbtiRepository hbtiRepository;

    public List<TrainerMatchResponseDTO> findMatchingTrainersByUserId(Long userId) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 사용자의 HBTI 조회
        HBTI userHBTI = hbtiRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("HBTI not found for user: " + userId));

        String userDistrict = extractDistrict(user.getAddress());
        List<String> hbtiTypes = List.of(userHBTI.getHbti());

        // 매칭되는 트레이너 찾기
        return trainerProfileRepository.findMatchingTrainersWithFetch(userDistrict, hbtiTypes)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private TrainerMatchResponseDTO convertToDTO(TrainerProfile trainerProfile) {
        User trainer = trainerProfile.getTrainer();

        // trainer의 id로 HBTI 정보 조회
        HBTI trainerHBTI = hbtiRepository.findByUserId(trainer.getId())
                .orElse(null);

        return TrainerMatchResponseDTO.builder()
                .trainerId(trainer.getId())
                .trainerName(trainer.getUsername())
                .nickname(trainer.getNickname())
                .hbti(trainerHBTI != null ? trainerHBTI.getHbti() : null)
                .gymName(trainer.getGym().getName())
                .profileImage(trainer.getProfileImage())
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