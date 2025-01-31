package com.lec.spring.training.controller;

import com.lec.spring.training.DTO.ReviewResponseDTO;
import com.lec.spring.training.domain.Review;
import com.lec.spring.training.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<ReviewResponseDTO>> getTrainerReviews(@PathVariable Long trainerId) {
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByTrainerId(trainerId);
        return reviews.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(reviews);
    }


    @PostMapping("/training/{trainingId}")
    public ResponseEntity<Review> createReview(
            @PathVariable Long trainingId,
            @RequestBody Review review) {
        try {
            return ResponseEntity.ok(reviewService.createReview(trainingId, review));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
