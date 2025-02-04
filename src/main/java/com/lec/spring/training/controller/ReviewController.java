package com.lec.spring.training.controller;

import com.lec.spring.base.config.PrincipalDetails;
import com.lec.spring.base.domain.User;
import com.lec.spring.training.DTO.ReviewResponseDTO;
import com.lec.spring.training.domain.Review;
import com.lec.spring.training.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/training/{trainerId}")
    public ResponseEntity<List<ReviewResponseDTO>> getTrainerReviews(
            @PathVariable Long trainerId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long userId = (principalDetails != null) ? principalDetails.getUser().getId() : null;
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByTrainerId(trainerId, userId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/training/{trainingId}")
    public ResponseEntity<?> createReview(
            @PathVariable Long trainingId,
            @RequestBody Review review,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        try {
            if (principalDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            Review createdReview = reviewService.createReview(
                    trainingId,
                    review,
                    principalDetails.getUser().getId()
            );

            return ResponseEntity.ok(createdReview);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body("Training을 찾을 수 없습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User user) {
        try {
            reviewService.deleteReview(reviewId, user.getId());
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
