package com.lec.spring.training.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lec.spring.base.config.PrincipalDetails;
import com.lec.spring.base.domain.User;
import com.lec.spring.base.service.UserService;
import com.lec.spring.training.DTO.SkillsDTO;
import com.lec.spring.training.DTO.TrainerProfileDTO;
import com.lec.spring.training.DTO.TrainerProfileReadDTO;
import com.lec.spring.training.domain.TrainerProfile;
import com.lec.spring.training.service.TrainerDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class MyPageController{

    private final TrainerDetailService trainerDetailService;
    private final UserService userService;

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

    //[트레이너 상세페이지 회원정보 불러오기]
    @GetMapping("/member/detail")
    public ResponseEntity<Object> getMemberDetail(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("principal details : " + principalDetails);
        User user = userService.findByUsername(principalDetails.getUsername());
        System.out.println("💿현재 로그인한 회원 : " + user);
        return ResponseEntity.ok(user);
    }

    // [이전 정보 불러오기]
    @GetMapping("/member/update-detail")
    public ResponseEntity<Object> updateMemberDetail(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        TrainerProfileReadDTO userProfile = trainerDetailService.getTrainerProfileById(principalDetails.getUser().getId());
        System.out.println("현재유저 : " + principalDetails.getUser().getUsername() + " 현재 유저 프로필 : " + userProfile);
        return ResponseEntity.ok(userProfile);

    }


    // [트레이너 상세페이지 작성]
    /*메소드와 메소드 사이에 정보를 보낼 때는 매개변수로 보내는 것을 잊지말자.!*/
    @PostMapping(value = "/member/detail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createTrainerProfile(
            @ModelAttribute TrainerProfileDTO trainerProfileDTO,
            @AuthenticationPrincipal PrincipalDetails user,
            @RequestParam("skills") List<String> skills,
            @RequestPart(required = false) List<MultipartFile> image
    ) {
        try {
            System.out.println(".....");
            System.out.println(" ##############삭제될 데이터 : " + trainerProfileDTO.getDeletedSkillsId());
            System.out.println();
            System.out.println("현재 로그인한 회원 : " + user.getUsername());

            // 비어있는 필드를 체크 (예시: trainerId가 없으면 400 오류)
            if (trainerProfileDTO.getTrainerId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("트레이너 ID가 필요합니다.");
            }

            // 트레이너 프로필 생성 서비스 호출
            boolean result = trainerDetailService.createTrainerProfile(trainerProfileDTO, user, skills, image);
            System.out.println("skills:" + skills + "image:" + image);

            // 결과 반환
            if (result) {
                return ResponseEntity.ok("트레이너 프로필이 성공적으로 등록되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("트레이너 프로필 등록에 실패했습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다. 다시 시도해주세요.");
        }
    }



//    // [트레이너 상세페이지 수정]
//    @PatchMapping(value = "/member/detail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<Boolean> updateTrainerProfile(
//            @ModelAttribute TrainerProfileDTO trainerProfileDTO,
//            @AuthenticationPrincipal PrincipalDetails user,
//            @RequestParam("skills") List<String> skills,
//            @RequestPart(required = false) List<MultipartFile> image
//
//    ) throws IOException {
//        System.out.println("🚀 skills: " + skills);
//        System.out.println("🚀 images count: " + image.size());
//        System.out.println(" deletedcertifications : " + trainerProfileDTO.getDeletedSkillsId());
//
//        boolean result = trainerDetailService.updateTrainerProfile(trainerProfileDTO,image);
//        if(result){
//            return new ResponseEntity<>(true, HttpStatus.OK);
//        }else{
//            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
//        } }





}// MyPageController
