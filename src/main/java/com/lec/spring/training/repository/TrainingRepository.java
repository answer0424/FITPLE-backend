package com.lec.spring.training.repository;

import com.lec.spring.training.domain.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    int findByUserIdAndTrainerIdEquals(Long studentId, Long trainerId);

    boolean existsByUserIdAndTrainerIdEquals(Long userId, Long trainerId);

    @Query("SELECT t FROM Training t WHERE t.trainer.id = :trainerId")
    List<Training> findByTrainerId(@Param("trainerId") Long trainerId);
}
