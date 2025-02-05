package com.lec.spring.training.service;

import com.lec.spring.base.config.PrincipalDetails;
import com.lec.spring.training.DTO.SkillsDTO;
import com.lec.spring.training.DTO.TrainerProfileDTO;
import com.lec.spring.training.DTO.TrainerProfileReadDTO;
import com.lec.spring.training.domain.Certification;
import com.lec.spring.training.domain.TrainerProfile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TrainerDetailService {
    // 트레이너 프로필 생성
    boolean createTrainerProfile(TrainerProfileDTO trainerProfile, PrincipalDetails user, List<String> skills, List<MultipartFile> files) throws IOException;


    // 트레이너 프로필 수정
    boolean updateTrainerProfile(List<SkillsDTO> certificationSkills, TrainerProfileDTO trainerProfileDTO) throws IOException;

    // 특정 트레이너 ID로 트레이너 프로필 조회 (DTO 변환)
    TrainerProfileReadDTO getTrainerProfileById(Long trainerId);






}// end TrainerDetailService
