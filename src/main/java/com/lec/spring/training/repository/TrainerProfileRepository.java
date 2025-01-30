package com.lec.spring.training.repository;

import com.lec.spring.training.domain.TrainerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerProfileRepository extends JpaRepository<TrainerProfile, Long> {
}
