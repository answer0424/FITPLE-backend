package com.lec.spring.training.service;

import com.lec.spring.base.domain.User;
import com.lec.spring.training.domain.Certification;
import com.lec.spring.training.domain.GrantStatus;
import com.lec.spring.training.domain.TrainerProfile;
import com.lec.spring.training.repository.TrainerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TrainerDetailServiceImple {

    private final TrainerProfileRepository trainerProfileRepository;

    public TrainerDetailServiceImple(TrainerProfileRepository trainerProfileRepository) {
        this.trainerProfileRepository = trainerProfileRepository;
    }

    // #트레이너 프로필 생성

    public TrainerProfile createTrainerProfile(User trainer, Integer perPrice, String skills, String content,
                                               LocalDate career, GrantStatus isAccess, List<Certification> certifications) {
        TrainerProfile trainerProfile = TrainerProfile.builder()
                .trainer(trainer)
                .perPrice(perPrice)
                .content(content)
                .career(career)
                .isAccess(isAccess)
                .build();

        if (certifications != null && !certifications.isEmpty()) {
            trainerProfile.addCertificationList(certifications.toArray(new Certification[0]));
        }

        return trainerProfileRepository.save(trainerProfile);
    }


    //# 전체 트레이너 프로필 조회
    @Transactional(readOnly = true)
    public List<TrainerProfile> getAllTrainerProfiles() {
        return trainerProfileRepository.findAll();
    }

    //#트레이너 id로 조회
    @Transactional(readOnly = true)
    public Optional<TrainerProfile> getTrainerProfileById(Long id) {
        return trainerProfileRepository.findById(id);
    }

    // # 트레이너 정보 수정
    public TrainerProfile updateTrainerProfile(Long id, Integer perPrice, String skills, String content,
                                               LocalDate career, GrantStatus isAccess, List<Certification> certifications) {
        TrainerProfile trainerProfile = trainerProfileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 트레이너 프로필이 존재하지 않습니다."));

        if (perPrice != null) trainerProfile.setPerPrice(perPrice);
        if (content != null) trainerProfile.setContent(content);
        if (career != null) trainerProfile.setCareer(career);
        if (isAccess != null) trainerProfile.setIsAccess(isAccess);

        if (certifications != null) {
            trainerProfile.getCertificationList().clear();
            trainerProfile.addCertificationList(certifications.toArray(new Certification[0]));
        }

        return trainerProfileRepository.save(trainerProfile);
    }
}// end TrainerDetailServiceImple
