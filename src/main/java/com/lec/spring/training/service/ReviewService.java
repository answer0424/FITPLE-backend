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

    public List<ReviewResponseDTO> getReviewsByTrainerId(Long trainerId, Long userId) {
        // 해당 트레이너의 모든 트레이닝 정보를 가져옴
        List<Training> allTrainings = trainingRepository.findTrainingsByTrainerId(trainerId);

        // 이미 작성된 리뷰들을 가져옴
        List<Review> existingReviews = reviewRepository.findReviewsByTrainerId(trainerId);

        return allTrainings.stream()
                .map(training -> {
                    User user = training.getUser();
                    User trainer = training.getTrainer();

                    // 해당 트레이닝에 대한 리뷰가 있는지 확인
                    Review existingReview = existingReviews.stream()
                            .filter(review -> review.getTraining().getId().equals(training.getId()))
                            .findFirst()
                            .orElse(null);

                    if (existingReview != null) {
                        return ReviewResponseDTO.builder()
                                .id(existingReview.getId())
                                .trainingId(training.getId())
                                .userId(training.getUser().getId())
                                .username(user.getUsername())
                                .trainerName(trainer.getUsername())
                                .userProfileImage(user.getProfileImage())
                                .trainerProfileImage(trainer.getProfileImage())
                                .rating(existingReview.getRating())
                                .content(existingReview.getContent())
                                .createdAt(existingReview.getCreatedAt())
                                .build();
                    } else {
                        return ReviewResponseDTO.builder()
                                .trainingId(training.getId())
                                .userId(user.getId())
                                .username(user.getUsername())
                                .trainerName(trainer.getUsername())
                                .userProfileImage(user.getProfileImage())
                                .trainerProfileImage(trainer.getProfileImage())
                                .build();
                    }
                })
                .collect(Collectors.toList());
    }

    public Review createReview(Long trainingId, Review review, Long userId) {
        // trainingId에 해당하는 Training 엔티티 조회
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException("Training이 없습니다."));

        // Training과 연관된 userId 검증
        if (!training.getUser().getId().equals(userId)) {
            throw new IllegalStateException("이 Training에 대한 리뷰 작성 권한이 없습니다.");
        }

        // Training에 이미 리뷰가 있는지 확인
        if (reviewRepository.existsByTrainingId(trainingId)) {
            throw new IllegalStateException("이미 리뷰가 작성되어 있습니다.");
        }

        // 리뷰 생성
        review.setTraining(training);
        return reviewRepository.save(review);
    }


    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));

        // 리뷰 작성자만 삭제 가능하도록
        if (!review.getTraining().getUser().getId().equals(userId)) {
            throw new IllegalStateException("이 리뷰를 삭제할 권한이 없습니다.");
        }

        reviewRepository.deleteById(reviewId);
    }
}