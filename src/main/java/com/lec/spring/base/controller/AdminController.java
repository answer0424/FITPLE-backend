package com.lec.spring.base.controller;

import com.lec.spring.base.domain.User;
import com.lec.spring.base.service.UserService;
import com.lec.spring.training.DTO.TrainerProfileReadDTO;
import com.lec.spring.training.domain.Review;
import com.lec.spring.training.service.ReviewService;
import com.lec.spring.training.service.TrainerDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ReviewService reviewService;
    private final TrainerDetailService trainerDetailService;

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(direction), sortBy != null ? sortBy : "id"));
        return ResponseEntity.ok(userService.getAllStudents(pageable));
    }

    @GetMapping("/trainers")
    public ResponseEntity<Page<User>> getTrainers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(direction), sortBy != null ? sortBy : "id"));
        return ResponseEntity.ok(userService.getAllTrainers(pageable));
    }

    @GetMapping("/trainers/{trainerId}/profile")
    public ResponseEntity<TrainerProfileReadDTO> getTrainerProfile(@PathVariable Long trainerId) {
        return ResponseEntity.ok(trainerDetailService.getTrainerProfileById(trainerId));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId, @RequestParam String role) {
        if ("ROLE_TRAINER".equals(role)) {
            userService.deleteTrainer(userId);
        } else if ("ROLE_STUDENT".equals(role)) {
            userService.deleteStudent(userId);
        } else {
            return ResponseEntity.badRequest().body("Invalid role specified");
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reviews")
    public ResponseEntity<Page<Review>> getReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(direction), sortBy != null ? sortBy : "id"));
        return ResponseEntity.ok(reviewService.getAllReviewsWithDetails(pageable));
    }

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<Review> getReviewDetail(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReviewDetails(reviewId));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReviewByAdmin(reviewId);
        return ResponseEntity.ok().build();
    }
}
