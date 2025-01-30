package com.lec.spring.training.repository;

import com.lec.spring.training.domain.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
}// end CertificationRepository
