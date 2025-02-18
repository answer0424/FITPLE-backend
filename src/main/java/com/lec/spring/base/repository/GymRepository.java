package com.lec.spring.base.repository;

import com.lec.spring.base.domain.Gym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GymRepository  extends JpaRepository<Gym, Long> {
    Optional<Gym> findById(Long gymId); // ✅ Gym의 ID로 조회

    Gym findByAddress(String address);
}
