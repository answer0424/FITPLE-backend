package com.lec.spring.training.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lec.spring.base.config.PrincipalDetails;
import com.lec.spring.training.DTO.SkillsDTO;
import com.lec.spring.training.DTO.TrainerProfileDTO;
import com.lec.spring.training.service.TrainerDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class MyPageController{

    private final TrainerDetailService trainerDetailService;

    //- 내 일정 띄우기


    //- 오늘의 일정 띄우기


    //- 스탬프 상태 변경

    
    //- 스탬프 띄우기


    //- 쿠폰 사용 기능


    //- 트레이너 별 쿠폰 페이지 변경 기능


    //- 남은 pt 횟수 불러오기


    //- 내 회원 목록 불러오기


    //- 채팅 목록에서 회원 이름 검색


    //- 트레이닝에 추가하기


    //- 일정 추가 기능


    //- 회원 일정 불러오기


    // [트레이너 상세페이지 작성]
    /*메소드와 메소드 사이에 정보를 보낼 때는 매개변수로 보내는 것을 잊지말자.!*/
    @PostMapping(value = "/member/detail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Boolean> createTrainerProfile(
            @ModelAttribute("trainerProfileDTO") TrainerProfileDTO trainerProfileDTO,
            @AuthenticationPrincipal PrincipalDetails user,
            @ModelAttribute("skills") List<String> skills,
            @RequestPart("img") List<MultipartFile> image
    ) throws JsonProcessingException {



        // 비어있는 필드를 체크 (예시: trainerId가 없으면 400 오류)
        if(trainerProfileDTO.getTrainerId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // 트레이너 프로필 생성 서비스 호출
        boolean result = trainerDetailService.createTrainerProfile(trainerProfileDTO, user, skills, image);


        // 결과 반환
        if(result){
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // [트레이너 상세페이지 수정]
    @PatchMapping(value = "/member/detail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Boolean> updateTrainerProfile(
            @ModelAttribute("trainerProfileDTO") TrainerProfileDTO trainerProfileDTO,
            @AuthenticationPrincipal PrincipalDetails user,
            @ModelAttribute("skills") List<String> skills,
            @RequestPart(value = "img", required = false) List<MultipartFile> image

    ) throws IOException {
        boolean result = trainerDetailService.updateTrainerProfile(trainerProfileDTO,skills,image);
        if(result){
            return new ResponseEntity<>(true, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




}// MyPageController
