package com.lec.spring.training.service;

import com.lec.spring.base.config.PrincipalDetailService;
import com.lec.spring.base.domain.User;
import com.lec.spring.base.repository.UserRepository;
import com.lec.spring.training.DTO.*;
import com.lec.spring.training.domain.Reservation;
import com.lec.spring.training.domain.ReservationStatus;
import com.lec.spring.training.domain.Training;
import com.lec.spring.training.repository.ReservationRepository;
import com.lec.spring.training.repository.TrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MyPageServiceImpl implements MyPageService {

    private final ReservationRepository reservationRepository;
    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;



    @Override
    public List<MonthReservationDTO> filterSchedulesByMonth(Long userid, int year, int month) {
        User user = userRepository.findById(userid).orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾지 못했습니다.")); // 현재 로그인한 유저

        if (user != null) {
            return reservationRepository.findReservationsByUserAndMonth(user.getId(), year, month);
        } else {
            return null;
        }
    }

    @Override
    public List<TodayReservationDTO> filterSchedulesByDay(String username, LocalDateTime date) {
        User user = userRepository.findByUsername(username);

        if (user != null) {
            return reservationRepository.findTodayReservationsByUser(user.getId(), date);
        } else {
            return null;
        }
    }

    @Override
    public boolean updateStampStatus(String status, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new IllegalArgumentException("예약이 없습니다."));

        reservation.setStatus(ReservationStatus.valueOf(status));
        reservationRepository.save(reservation);

        if (reservation.getStatus().equals(ReservationStatus.운동완료)) {
            Training training = trainingRepository.findById(reservation.getTraining().getId()).orElseThrow(() -> new IllegalArgumentException("스탬프 추가에 실패했습니다"));
            training.setTotal_stamps(training.getTotal_stamps() + 1);
        }

        return true;
    }

    @Override
    public int showStampList(Long studentId, Long trainerId) {
        long trainingId = findTrainingId(studentId, trainerId);

        try {
            Training training = trainingRepository.findById(trainingId).orElseThrow();

            if (training.getTotal_stamps() >= 10) { //스탬프가 10개 이상이면 쿠폰으로 변환
                int coupon = training.getTotal_stamps() / 10;

                training.setTotal_stamps(training.getTotal_stamps() - (10 * coupon));
                training.setCoupons(training.getCoupons() + coupon);
                trainingRepository.saveAndFlush(training);
            }

            return training.getTotal_stamps();
        }catch (Exception e) {
            return -1;
        }
    }

    @Override
    public boolean useCoupon(Long studentId, Long trainerId) {
        long trainingId = findTrainingId(studentId, trainerId);


        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰 확인에 실패했습니다."));

        if (training.getCoupons() > 0) {
            training.setCoupons(training.getCoupons() - 1);
            training.setTimes(training.getTimes() + 1);
            trainingRepository.saveAndFlush(training);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public CouponPageDTO changeCouponPageByTrainer(Long studentId, Long trainerId) {

        Training training = trainingRepository.findById(findTrainingId(studentId, trainerId)).orElseThrow(() -> new IllegalArgumentException("쿠폰 페이지 불러오기에 실패했습니다"));
        List<User> ids = new ArrayList<>();
        trainingRepository.findByUserId(studentId)
                .forEach(t ->
                        ids.add(t.getTrainer())
                );

        return CouponPageDTO.builder()
                .coupons(training.getCoupons())
                .times(training.getTimes())
                .nickname(training.getTrainer().getNickname())
                .trainerIds(ids)
                .build();
    }

    @Override
    public int getPtCount(Long studentId, Long trainerId) {
        Training training = trainingRepository.findById(findTrainingId(studentId, trainerId))
                .orElseThrow(() -> new IllegalArgumentException("남은 횟수 불러오기에 실패했습니다"));

        return training.getTimes();
    }

    @Override
    public int setPtCount(Long studentId, Long trainerId, int times) {
        Training training = trainingRepository.findById(findTrainingId(studentId, trainerId))
                .orElseThrow(() -> new IllegalArgumentException("남은 횟수 불러오기에 실패했습니다"));

        training.setTimes(training.getTimes() + times);

        return trainingRepository.saveAndFlush(training).getTimes();
    }

    @Override
    public List<StudentListDTO> getMyStudentList(Long trainerId) {
        List<StudentListDTO> studentList = new ArrayList<>();

        trainingRepository.findByTrainerId(trainerId).forEach(e ->
                studentList.add(
                        StudentListDTO.builder()
                                .times(e.getTimes())
                                .nickname(e.getUser().getNickname())
                                .userId(e.getUser().getId())
                                .build()
                )
        );

        return studentList;
    }

    @Override
    public CouponPageDTO getMyTrainerPage(Long userId) {
        Long trainerId = trainingRepository.findByUserId(userId).get(0).getTrainer().getId();

        return changeCouponPageByTrainer(userId, trainerId);
    }

    @Override
    public StudentListDTO findStudentByChats(Long trainerId, String studentName) {
        try {
            long studentId = userRepository.findByUsername(studentName).getId();

            //TODO : 채팅 기반 검색

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void addTraining(Long studentId, Long trainerId) {
        Training training = Training.builder()
                .user(userRepository.findById(studentId)
                        .orElseThrow(() -> new IllegalArgumentException("회원 목록에서 검색에 실패했습니다")))
                .trainer(userRepository.findById(trainerId)
                        .orElseThrow(() -> new IllegalArgumentException("트레이너 목록에서 검색에 실패했습니다")))
                .times(0)
                .build();

        trainingRepository.saveAndFlush(training);

    }

    @Override
    public void addSchedule(CreateReservationDTO reservationDTO, long studentId) {
        Reservation reservation = Reservation.builder()
                .date(reservationDTO.getDate())
                .training(trainingRepository.findById(
                        findTrainingId(studentId, reservationDTO.getTrainingId())).orElseThrow(() -> new IllegalArgumentException("일정 등록에 실패했습니다.")))
                .build();

        reservationRepository.save(reservation);
    }

    @Override
    public void deleteSchedule(long reservationId) {
        reservationRepository.delete(reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 스케줄을 찾지 못했습니다")));
        reservationRepository.flush();
    }

    @Override
    public List<MonthReservationDTO> getSchedulesByMember(Long studentId, Long trainerId, int year, int month) {
        return reservationRepository.findReservationsByStudentAndMonth(studentId, trainerId, year, month);
    }

    @Override
    public long findTrainingId(Long studentId, Long trainerId) {
        return trainingRepository.findByUserIdAndTrainerIdEquals(studentId, trainerId);
    }
}
