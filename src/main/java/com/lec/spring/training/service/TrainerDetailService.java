package com.lec.spring.training.service;

import com.lec.spring.base.config.PrincipalDetails;
import com.lec.spring.training.DTO.TrainerProfileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TrainerDetailService {
    // 트레이너 프로필 생성
    boolean createTrainerProfile(TrainerProfileDTO trainerProfile, PrincipalDetails user, List<String> skills, List<MultipartFile> files);

//    List<TrainerProfile> getAllTrainerProfiles();

    // 닉네임 기준 프로필 검색/상세 페이지 완성 시 수정
    TrainerProfileDTO getTrainerProfileByNickname(String nickname, PrincipalDetails userss);

    // 트레이너 프로필 수정
    boolean updateTrainerProfile(TrainerProfileDTO trainerProfile,  List<String> skills, List<MultipartFile> images) throws IOException;



}// end TrainerDetailService
