package com.lec.spring.training.service;

import com.lec.spring.base.domain.User;
import com.lec.spring.base.repository.UserRepository;
import com.lec.spring.base.service.mapper.UserMapper;
import com.lec.spring.chat.repository.UserChatRepository;
import com.lec.spring.training.DTO.*;
import com.lec.spring.training.DTO.output.CouponPageTrainerList;
import com.lec.spring.training.DTO.output.StudentDTO;
import com.lec.spring.training.domain.Reservation;
import com.lec.spring.training.domain.ReservationStatus;
import com.lec.spring.training.domain.Training;
import com.lec.spring.training.repository.ReservationRepository;
import com.lec.spring.training.repository.TrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MyPageServiceImpl implements MyPageService {

    private final ReservationRepository reservationRepository;
    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserChatRepository userChatRepository;



    @Override
    public List<MonthReservationDTO> filterSchedulesByMonth(Long userid, int year, int month) {
        User user = userRepository.findById(userid)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾지 못했습니다.")); // 현재 로그인한 유저
//        System.out.println(year+","+month);
        if (user != null) {
            //            System.out.println("\n\n\n\n\n"+reservationsByUserAndMonth);
            return reservationRepository.findReservationsByUserAndMonth(userid, year, month);
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
    @Transactional
    public boolean updateStampStatus(String status, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new IllegalArgumentException("예약이 없습니다."));

        try {
            reservation.setStatus(ReservationStatus.valueOf(status));

            if (status.equals("운동중")) {
                reservation.setStartTime(LocalTime.now());
            }

            if (status.equals("운동완료")) {
                // 두 시간 간의 차이 계산
                Duration duration = Duration.between(reservation.getStartTime(), LocalTime.now());
                int exerciseTime = (int) duration.toMinutes();
                reservation.setExerciseTime(exerciseTime);

                Training training = trainingRepository.findById(reservation.getTraining().getId())
                        .orElseThrow(() -> new IllegalArgumentException("스탬프 추가에 실패했습니다"));
                int stamp = training.getTotal_stamps() + 1;
                if(stamp >= 10) {
                    //스탬프  - 쿠폰 변환
                    System.out.println("들어옴?");
                    StampToCoupon(training, stamp);
                } else {
                    training.setTotal_stamps(training.getTotal_stamps() + 1);
                }

                training.setTimes(training.getTimes() - 1);
            }

        } catch (IllegalArgumentException e) {
            throw e;
        }
        return true;
    }

    @Override
    public int StampToCoupon(Training training, int stamp) {
        try {
            int coupon = (stamp) / 10;

            training.setTotal_stamps(stamp - (10 * coupon));
            training.setCoupons(training.getCoupons() + coupon);
            trainingRepository.saveAndFlush(training);

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

        Long id = findTrainingId(studentId, trainerId);

        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰 페이지 불러오기에 실패했습니다"));
        List<CouponPageTrainerList> ids = new ArrayList<>();
        trainingRepository.findByUserId(studentId)
                .forEach(t ->
                        ids.add(userMapper.toCouponPageTrainerListDto(t.getTrainer()))
                );

        return CouponPageDTO.builder()
                .coupons(training.getCoupons())
                .times(training.getTimes())
                .nickname(training.getTrainer().getNickname())
                .gymName(training.getTrainer().getGym().getName())
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

        System.out.println("studentList: " + studentList);

        return studentList;
    }

    @Override
    public CouponPageDTO getMyTrainerPage(Long userId) {
        Long trainerId = trainingRepository.findByUserId(userId).get(0).getTrainer().getId();

        return changeCouponPageByTrainer(userId, trainerId);
    }


    @Override
    public List<StudentListDTO> findStudentByChats(Long trainerId) {
        // 트레이너가 속한 채팅 목록 가져오기
        List<Long> chatIds = userChatRepository.findChatIdsByUserId(trainerId);

        if (chatIds.isEmpty()) {
            System.out.println("해당 트레이너가 속한 채팅이 없습니다.");
            return new ArrayList<>(); // 빈 리스트 반환
        }

        // 트레이너와 같은 채팅방에 참여한 학생 검색
        List<User> students = userChatRepository.findStudentsInChats(chatIds);
        System.out.println(trainerId + " 트레이너의 학생들입니다 : " + students);

        // 학생 정보를 StudentListDTO 리스트로 변환
        List<StudentListDTO> studentListDTOs = students.stream()
                .map(student -> new StudentListDTO(
                        student.getId(),
                        student.getNickname(),
                        0,  // times 필드 초기값 (필요에 따라 변경)
                        List.of(new StudentDTO(student.getId(), student.getNickname()))
                ))
                .collect(Collectors.toList());

        System.out.println("studentListDTOs : " + studentListDTOs);

        return studentListDTOs;
    }


    @Override
    public void addTraining(Long studentId, Long trainerId) {
        if(
                trainingRepository.findByUserIdAndTrainerIdEquals(studentId, trainerId) != null
        ) {
            throw new IllegalArgumentException("이미 수강 중인 회원입니다.");
        }

        Training training = Training.builder()
                .user(userRepository.findById(studentId)
                        .orElseThrow(() -> new IllegalArgumentException("회원 목록에서 검색에 실패했습니다")))
                .trainer(userRepository.findById(trainerId)
                        .orElseThrow(() -> new IllegalArgumentException("트레이너 목록에서 검색에 실패했습니다")))
                .times(0)
                .build();

        trainingRepository.saveAndFlush(training);

    }

//@Override
//public StudentListDTO findStudentByChats(Long userId) {
//    try {
//        // 트레이너가 속한 채팅 목록 가져오기
//        List<Long> chatIds = userChatRepository.findChatIdsByUserId(userId);
//        if (chatIds.isEmpty()) {
//            System.out.println("해당 트레이너가 속한 채팅이 없습니다.");
//            return new StudentListDTO(userId, "Unknown", 0, new ArrayList<>());
//        }
//
//        // 트레이너와 같은 채팅방에 참여한 학생 검색
//        List<User> students = userChatRepository.findStudentsInChats(chatIds, userId);
//        System.out.println(userId + " 트레이너의 학생들입니다 : " + students);
//
//        // 결과를 DTO로 변환
//        List<StudentDTO> studentDTOs = students.stream()
//                .map(s -> new StudentDTO(s.getId(), s.getNickname()))
//                .collect(Collectors.toList());
//
//        System.out.println("studentDTOs : " + studentDTOs);
//
//        // 반환할 StudentListDTO 생성
//        return new StudentListDTO(userId, "Trainer", studentDTOs.size(), studentDTOs);
//    } catch (Exception e) {
//        e.printStackTrace();
//        return new StudentListDTO(userId, "Unknown", 0, new ArrayList<>()); // 예외 발생 시 빈 리스트 반환
//    }
//}

    @Override
    public void addSchedule(CreateReservationDTO reservationDTO, long studentId) {
        Reservation reservation = Reservation.builder()
                .date(reservationDTO.getDate())
                .training(trainingRepository.findById(reservationDTO.getTrainingId())
                        .orElseThrow(() -> new IllegalArgumentException("일정 등록에 실패했습니다.")))
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
        Training training = trainingRepository.findByUserIdAndTrainerIdEquals(studentId, trainerId);
        return training.getId();
    }
}
