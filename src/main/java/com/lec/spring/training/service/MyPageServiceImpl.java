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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MyPageServiceImpl implements MyPageService {

    private final ReservationRepository reservationRepository;
    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;

    public MyPageServiceImpl(ReservationRepository reservationRepository, TrainingRepository trainingRepository, UserRepository userRepository, PrincipalDetailService principalDetailService) {
        this.reservationRepository = reservationRepository;
        this.trainingRepository = trainingRepository;
        this.userRepository = userRepository;
    }


    @Override
    public List<MonthReservationDTO> filterSchedulesByMonth(String username, int year, int month) {
        User user = userRepository.findByUsername(username); // 현재 로그인한 유저

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
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);

        if (reservation != null) {
            reservation.setStatus(ReservationStatus.valueOf(status));
            return true;
        } else {
            return false;
        }
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

        try {
            Training training = trainingRepository.findById(trainingId).orElseThrow();

            if (training.getCoupons() > 0) {
                training.setCoupons(training.getCoupons() - 1);
                training.setTimes(training.getTimes() + 1);
                trainingRepository.saveAndFlush(training);
            } else {
                return false;
            }
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    @Override
    public CouponPageDTO changeCouponPageByTrainer(Long studentId, Long trainerId) {
        try{
            Training training = trainingRepository.findById(findTrainingId(studentId, trainerId)).orElseThrow();
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
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int getPtCount(Long studentId, Long trainerId) {
        try{
            Training training = trainingRepository.findById(findTrainingId(studentId, trainerId)).orElseThrow();

            return training.getTimes();
        } catch (Exception e) {
            return 0;
        }
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

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean addTraining(Long studentId, Long trainerId) {
        try {
            Training training = Training.builder()
                    .user(userRepository.findById(studentId).orElseThrow())
                    .trainer(userRepository.findById(trainerId).orElseThrow())
                    .times(0)
                    .build();

            trainingRepository.saveAndFlush(training);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean addSchedule(CreateReservationDTO reservationDTO, long studentId) {
        try {
            Reservation reservation = Reservation.builder()
                    .date(reservationDTO.getDate())
                    .training(trainingRepository.findById(
                            findTrainingId(studentId, reservationDTO.getTrainingId())).orElseThrow())
                    .build();

            reservationRepository.save(reservation);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<MonthReservationDTO> getSchedulesByMember(Long studentId, Long trainerId, int year, int month) {

        MonthReservationDTO

        return List.of();
    }

    @Override
    public long findTrainingId(Long studentId, Long trainerId) {
        return trainingRepository.findByUserIdAndTrainerIdEquals(studentId, trainerId);
    }
}
