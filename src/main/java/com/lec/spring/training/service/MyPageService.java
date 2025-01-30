package com.lec.spring.training.service;

import com.lec.spring.training.DTO.CreateReservationDTO;
import com.lec.spring.training.DTO.MonthReservationDTO;
import com.lec.spring.training.DTO.StudentListDTO;
import com.lec.spring.training.DTO.TodayReservationDTO;
import com.lec.spring.training.domain.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MyPageService {
    //- 이 달의 일정 띄우기
    List<MonthReservationDTO> filterSchedulesByMonth(String username, int year, int month);

    //- 오늘의 일정 띄우기
    List<TodayReservationDTO> filterSchedulesByDay(String username, LocalDateTime date);

    //- 스탬프 상태 변경(시작, 완료 누르기)
    boolean updateStampStatus(String status, Long reservationId);

    //- 스탬프 띄우기
    int showStampList(Long studentId, Long trainerId);

    //- 쿠폰 사용 기능
    boolean useCoupon(Long studentId, Long trainerId);

    //- 트레이너 별 쿠폰 페이지 변경 기능
    void changeCouponPageByTrainer(Long studentId, Long trainerId);

    //- 남은 pt 횟수 불러오기
    int getPtCount(Long studentId, Long trainerId);

    //- 내 회원 목록 불러오기
    List<StudentListDTO> getMyStudentList(Long trainerId);

    //- 채팅 목록에서 회원 이름 검색
    StudentListDTO findStudentByChats(Long trainerId, String studentName);

    //- 트레이닝에 추가하기
    void addTraining(Long studentId, Long trainerId);

    //- 일정 추가 기능
    void addSchedule(CreateReservationDTO reservationDTO);

    //- 회원별 일정 불러오기
    List<MonthReservationDTO> getSchedulesByMember(Long studentId, Long trainerId);

    //- 트레이닝 id 찾기
    int findTrainingId(Long studentId, Long trainerId);


} //end MyPageService
