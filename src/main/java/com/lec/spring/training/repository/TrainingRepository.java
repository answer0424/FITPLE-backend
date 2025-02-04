package com.lec.spring.training.repository;

import com.lec.spring.training.domain.Training;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    Training findByUserIdAndTrainerIdEquals(Long studentId, Long trainerId);

    List<Training> findByTrainerId(Long trainerId);

    //해당 유저의 트레이닝 목록
    List<Training> findByUserId(Long userId);


}// end TrainingRepository
