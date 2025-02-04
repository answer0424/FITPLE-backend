package com.lec.spring.training.repository;

import com.lec.spring.training.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.training.trainer.id = :trainerId ORDER BY r.createdAt DESC")
    List<Review> findReviewsByTrainerId(@Param("trainerId") Long trainerId);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Review r WHERE r.training.id = :trainingId")
    boolean existsByTrainingId(@Param("trainingId") Long trainingId);
}






