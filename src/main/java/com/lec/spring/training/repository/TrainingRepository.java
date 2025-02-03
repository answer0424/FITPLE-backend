package com.lec.spring.training.repository;

import com.lec.spring.training.domain.Training;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    int findByUserIdAndTrainerIdEquals(Long studentId, Long trainerId);

    List<Long> findByTrainerId(Long trainerId);


}// end TrainingRepository
