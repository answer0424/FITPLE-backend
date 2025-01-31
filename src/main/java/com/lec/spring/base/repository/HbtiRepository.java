package com.lec.spring.base.repository;

import com.lec.spring.base.domain.HBTI;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HbtiRepository extends JpaRepository<HBTI, Long> {
    Optional<HBTI> findByUserId(Long userId);
}
