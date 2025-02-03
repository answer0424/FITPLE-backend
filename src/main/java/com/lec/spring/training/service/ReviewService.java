package com.lec.spring.training.service;

import com.lec.spring.base.domain.User;
import com.lec.spring.training.DTO.ReviewResponseDTO;
import com.lec.spring.training.domain.Review;
import com.lec.spring.training.domain.Training;
import com.lec.spring.training.repository.ReviewRepository;
import com.lec.spring.training.repository.TrainingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final TrainingRepository trainingRepository;

    public ReviewService(ReviewRepository reviewRepository, TrainingRepository trainingRepository) {
        this.reviewRepository = reviewRepository;
        this.trainingRepository = trainingRepository;
    }

    public List<ReviewResponseDTO> getReviewsByTrainerId(Long trainerId) {
        return reviewRepository.findReviewsByTrainerId(trainerId)
                .stream()
                .map(review -> {
                    Training training = review.getTraining();
                    User user = training.getUser();
                    User trainer = training.getTrainer();

                    return ReviewResponseDTO.builder()
                            .id(review.getId())
                            .trainingId(training.getId())
                            .username(user.getUsername())
                            .trainerName(trainer.getUsername())
                            .userProfileImage(user.getProfileImage())
                            .trainerProfileImage(trainer.getProfileImage())
                            .rating(review.getRating())
                            .content(review.getContent())
                            .createdAt(review.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }


    public Review createReview(Long trainingId, Review review) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException("Training이 없습니다."));

        review.setTraining(training);
//        review.setCreatedAt(new Date());
        return reviewRepository.save(review);
    }


    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}