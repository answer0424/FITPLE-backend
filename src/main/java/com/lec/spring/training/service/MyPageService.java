package com.lec.spring.training.service;

import com.lec.spring.training.DTO.*;
import com.lec.spring.training.domain.Training;

import java.time.LocalDateTime;
import java.util.List;

public interface MyPageService {
    //- 이 달의 일정 띄우기
    List<MonthReservationDTO> filterSchedulesByMonth(Long userid, int year, int month);

    //- 오늘의 일정 띄우기
    List<TodayReservationDTO> filterSchedulesByDay(String username, LocalDateTime date);

    //- 스탬프 상태 변경(시작, 완료 누르기)
    boolean updateStampStatus(String status, Long reservationId);

    //- 스탬프 띄우기
    int StampToCoupon(Training training, int stamp);

    //- 쿠폰 사용 기능
    boolean useCoupon(Long studentId, Long trainerId);

    //- 트레이너 별 쿠폰 페이지 변경 기능
    CouponPageDTO changeCouponPageByTrainer(Long studentId, Long trainerId);

    //- 남은 pt 횟수 불러오기
    int getPtCount(Long studentId, Long trainerId);

    //- pt 횟수 변경
    int setPtCount(Long studentId, Long trainerId, int times);

    //- 내 회원 목록 불러오기
    List<StudentListDTO> getMyStudentList(Long trainerId);

    //- 인덱스 0번 트레이너의 쿠폰 페이지
    CouponPageDTO getMyTrainerPage(Long userId);

    //- 트레이너 채팅방에 존재하는 회원리스트
    List<StudentListDTO> findStudentByChats(Long trainerId);

    //- 트레이닝에 추가하기
    void addTraining(Long studentId, Long trainerId);

    //- 일정 추가 기능
    void addSchedule(CreateReservationDTO reservationDTO, long studentId);

    //- 일정 추가 기능
    void deleteSchedule(long reservationId);

    //- 회원별 일정 불러오기
    List<MonthReservationDTO> getSchedulesByMember(Long studentId, Long trainerId, int year, int month);

    //- 트레이닝 id 찾기
    long findTrainingId(Long studentId, Long trainerId);



    // 트레이너 작성 페이지 (이전) 불러오기


} //end MyPageService
