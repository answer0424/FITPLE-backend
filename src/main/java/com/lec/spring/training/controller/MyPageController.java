package com.lec.spring.training.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lec.spring.base.DTO.MyPageUserInfoDTO;
import com.lec.spring.base.config.PrincipalDetails;
import com.lec.spring.base.service.HbtiService;
import com.lec.spring.base.service.UserService;
import com.lec.spring.training.DTO.*;
import com.lec.spring.training.DTO.input.TrainerIdStudentIdDTO;
import com.lec.spring.training.DTO.input.UpdateProfileImage;
import com.lec.spring.training.DTO.input.UpdateScheduleDTO;
import com.lec.spring.training.service.MyPageService;
import com.lec.spring.base.domain.User;
import com.lec.spring.base.service.UserService;
import com.lec.spring.training.DTO.SkillsDTO;
import com.lec.spring.training.DTO.TrainerProfileDTO;
import com.lec.spring.training.DTO.TrainerProfileReadDTO;
import com.lec.spring.training.domain.TrainerProfile;
import com.lec.spring.training.service.TrainerDetailService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MyPageController{

    private final TrainerDetailService trainerDetailService;
    private final MyPageService myPageService;
    private final UserService userService;
    private final HbtiService hbtiService;


    // 마이페이지 info 컴포넌트(좌측 내 정보)
    @GetMapping("/{userid}/info")
    public ResponseEntity<?> getMyPageUserInfo(@PathVariable Long userid) {
        try {
            MyPageUserInfoDTO userInfo = userService.getMyPageUserInfo(userid);
            System.out.println(userInfo);
            return new ResponseEntity<>(userInfo, HttpStatus.OK);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 학생 마이페이지 일정 조회 로직
    @GetMapping("/{userid}/calendar")
    public ResponseEntity<?> getStudentCalendar(@PathVariable Long userid,
                                                @RequestParam int year,
                                                @RequestParam int month) {
//        System.out.println("진입\n\n\n\n\n\n\n\n");
        try {
            List<MonthReservationDTO> monthDTO = myPageService.filterSchedulesByMonth(userid, year, month+1);
//            System.out.println(monthDTO);
            return new ResponseEntity<>(monthDTO, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 회원 정보 수정 페이지 조회 로직
    @GetMapping("/{userid}/edit")
    public ResponseEntity<?> editMemberInfo(@PathVariable Long userid) {
        try {
            MyPageUserInfoDTO userInfo = userService.getMyPageUserInfo(userid);
            return new ResponseEntity<>(userInfo, HttpStatus.OK);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 학생 마이페이지 스탬프 조회 로직
    @GetMapping("/{userid}/stamp")
    public ResponseEntity<?> getStudentStamp(@PathVariable Long userid) {
        try {
            CouponPageDTO couponPageDTO = myPageService.getMyTrainerPage(userid);

            return new ResponseEntity<>(couponPageDTO, HttpStatus.OK);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 학생 마이페이지에서 트레이너 변경 로직
    @GetMapping("/{userid}/stamp/trainer/{trainerId}")
    public ResponseEntity<?> changeTrainerForStamp(@PathVariable Long userid, @PathVariable Long trainerId) {
        try {
            CouponPageDTO couponPageDTO = myPageService.changeCouponPageByTrainer(userid, trainerId);

            return new ResponseEntity<>(couponPageDTO, HttpStatus.OK);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 트레이너 페이지 일정 등록 학생 목록 조회 로직
    @GetMapping("/{userid}/register")
    public ResponseEntity<?> registerSchedule(@PathVariable Long userid) {
        List<StudentListDTO> studentListDTO = myPageService.getMyStudentList(userid);
        if(!studentListDTO.isEmpty()) {

            return new ResponseEntity<>(studentListDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("수강생이 없습니다", HttpStatus.NO_CONTENT);
        }
    }

    // 트레이너 페이지에서 학생별 일정 등록 조회 로직
//    @GetMapping("/{userid}/register/student/{studentId}")
//    public ResponseEntity<?> registerScheduleForStudent(@PathVariable Long userid,
//                                                        @PathVariable Long studentId,
//                                                        @RequestParam int year,
//                                                        @RequestParam int month) {
//        List<MonthReservationDTO> monthReservationDTO =
//                myPageService.getSchedulesByMember(studentId, userid, year, month);
//
//        if(!monthReservationDTO.isEmpty()) {
//
//            return new ResponseEntity<>(monthReservationDTO, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>("해당 학생과 일정이 없습니다", HttpStatus.NO_CONTENT);
//        }
//    }

    // 트레이너 페이지에서 학생 검색 로직
    @GetMapping("/{userid}/register/search")
    public ResponseEntity<?> searchStudentsForSchedule(@PathVariable Long userid,
                                                       @RequestParam String studentName) {
        //TODO : 채팅 기반 검색
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    // 트레이너 페이지에서 학생별 일정 조회 로직
    @GetMapping("/{userid}/calendar/student/{studentId}")
    public ResponseEntity<?> getTrainerCalendarForStudent(@PathVariable Long userid,
                                                          @PathVariable Long studentId,
                                                          @RequestParam int year,
                                                          @RequestParam int month) {
        List<MonthReservationDTO> monthReservationDTO =
                myPageService.getSchedulesByMember(studentId, userid, year, month);

        if(!monthReservationDTO.isEmpty()) {

            return new ResponseEntity<>(monthReservationDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("해당 학생과 일정이 없습니다", HttpStatus.NO_CONTENT);
        }
    }

//     마이페이지에서 AI 프로필 사진 생성 요청 처리 로직
    @PostMapping("/{userid}/ai-creation")
    public ResponseEntity<?> createAIProfilePicture(@PathVariable String userid) {
        //TODO : AI 빨랑 해야하는대...
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    // 트레이너 페이지에서 일정 등록 추가 처리 로직
    @PostMapping("/register/add-schedule/{userId}")
    public ResponseEntity<?> addSchedule(@PathVariable Long userId,
                                         @RequestBody CreateReservationDTO reservationDTO) {
        try {
            myPageService.addSchedule(reservationDTO, userId);
            return new ResponseEntity<>("일정 등록에 성공했습니다", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 트레이너 페이지 회원 추가 처리 로직
    @PostMapping("/register/add-member")
    public ResponseEntity<?> addMemberToSchedule(@RequestBody TrainerIdStudentIdDTO DTO) {
        try {
            myPageService.addTraining(DTO.getStudentId(), DTO.getTrainerId());
            return new ResponseEntity<>("회원 추가에 성공했습니다", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 마이페이지에서 회원 정보 수정 처리 로직
    @PatchMapping("/mypage")
    public ResponseEntity<?> updateMemberInfo(@RequestBody MyPageUserInfoDTO newUserInfo) {
        try {
            userService.changeUserProfile(newUserInfo);
            return new ResponseEntity<>("", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 마이페이지에서 일정 상태 변경 처리 로직
    @PatchMapping("/schedule")
    public ResponseEntity<?> updateSchedule(@RequestBody UpdateScheduleDTO DTO) {
        System.out.println("\n\n\n\n\n\n\n");
        System.out.println(DTO);
        try {
            if(myPageService.updateStampStatus(DTO.getStatus(), DTO.getReservationId()))
                return new ResponseEntity<>("완료되었습니다", HttpStatus.OK);
            else return new ResponseEntity<>("변경 실패하였습니다 \n 다시 시도해주세요", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 마이페이지에서 프로필 사진 변경 처리 로직
    @PatchMapping("/profile-img")
    @Transactional
    public ResponseEntity<?> updateProfileImage(@RequestPart("userId") Long userId,
                                                @RequestPart("profileImage") MultipartFile profileImage,
                                                HttpServletRequest request) {
        String contentType = request.getContentType();
        System.out.println("Received Content-Type: " + contentType);
        System.out.println("Received userId: " + userId);
        System.out.println("Received profileImage: " + profileImage);
        try {
            userService.changeUserProfileImage(profileImage, userId);
            return new ResponseEntity<>("", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 마이페이지에서 AI 프로필 사진 변경 처리 로직
    @PatchMapping("/ai-img")
    public ResponseEntity<?> updateAIProfileImage(@RequestBody MultipartFile profileImage,
                                                  @RequestBody Long userId,
                                                  @RequestBody String AIPrompt) {
        //TODO
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    // 학생 마이페이지에서 쿠폰 사용 처리 로직
    @PatchMapping("/use-coupons")
    public ResponseEntity<?> useCoupons(@RequestBody TrainerIdStudentIdDTO DTO) {
        try {
            if(myPageService.useCoupon(DTO.getStudentId(), DTO.getTrainerId())){
                return new ResponseEntity<>("쿠폰 사용에 성공했습니다", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("쿠폰 사용에 실패했습니다", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 트레이너 페이지에서 PT 횟수 증감 처리 로직
    @PatchMapping("/pt-count")
    public ResponseEntity<?> updatePTCount(@RequestBody TrainerIdStudentIdDTO DTO) {
        try {
            return new ResponseEntity<>(myPageService.setPtCount(DTO.getStudentId(),
                    DTO.getTrainerId(), DTO.getTimes()), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

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

    // 회원 탈퇴 처리 로직
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteMember(@PathVariable Long userId) {
        try {
            userService.DeleteMember(userId);
            return new ResponseEntity<>("탈퇴처리 되었습니다", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 트레이너 페이지에서 일정 삭제 처리 로직
    @DeleteMapping("/calendar/delete-schedule")
    public ResponseEntity<?> deleteSchedule(@RequestBody Long reservationId) {
        try {
            myPageService.deleteSchedule(reservationId);
            return new ResponseEntity<>("삭제 되었습니다", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
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
