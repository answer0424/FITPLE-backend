package com.lec.spring.base.repository;

import com.lec.spring.base.domain.Gym;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GymRepository extends JpaRepository<Gym, Long> {
    Gym findByAddress(String address);
}
