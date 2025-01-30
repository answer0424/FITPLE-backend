package com.lec.spring.training.service;

import com.lec.spring.base.config.PrincipalDetailService;
import com.lec.spring.base.domain.User;
import com.lec.spring.base.repository.UserRepository;
import com.lec.spring.training.DTO.CreateReservationDTO;
import com.lec.spring.training.DTO.MonthReservationDTO;
import com.lec.spring.training.DTO.StudentListDTO;
import com.lec.spring.training.DTO.TodayReservationDTO;
import com.lec.spring.training.domain.Reservation;
import com.lec.spring.training.domain.ReservationStatus;
import com.lec.spring.training.domain.Training;
import com.lec.spring.training.repository.ReservationRepository;
import com.lec.spring.training.repository.TrainingRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
                trainingRepository.save(training);
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
                trainingRepository.save(training);
            } else {
                return false;
            }
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    @Override
    public void changeCouponPageByTrainer(Long studentId, Long trainerId) {

    }

    @Override
    public int getPtCount(Long studentId, Long trainerId) {
        return 0;
    }

    @Override
    public List<StudentListDTO> getMyStudentList(Long trainerId) {
        return List.of();
    }

    @Override
    public StudentListDTO findStudentByChats(Long trainerId, String studentName) {
        return null;
    }

    @Override
    public void addTraining(Long studentId, Long trainerId) {

    }

    @Override
    public void addSchedule(CreateReservationDTO reservationDTO) {

    }

    @Override
    public List<MonthReservationDTO> getSchedulesByMember(Long studentId, Long trainerId) {
        return List.of();
    }

    @Override
    public int findTrainingId(Long studentId, Long trainerId) {
        return trainingRepository.findByUserIdAndTrainerIdEquals(studentId, trainerId);
    }
}
