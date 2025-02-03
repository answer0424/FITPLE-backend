package com.lec.spring.base.repository;

import com.lec.spring.base.domain.Gym;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GymRepository  extends JpaRepository<Gym, Long> {
    Optional<Gym> findById(Long gymId); // ✅ Gym의 ID로 조회
}
