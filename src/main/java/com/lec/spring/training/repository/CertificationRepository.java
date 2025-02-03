package com.lec.spring.training.repository;

import com.lec.spring.training.domain.Certification;
import com.lec.spring.training.domain.CertificationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CertificationRepository extends JpaRepository<Certification, CertificationId> {


        @Modifying
        @Transactional
        @Query("DELETE FROM Certification c WHERE c.id IN ?1")
        void deleteAllByIdInBatch(List<Long> ids);
}// end CertificationRepository
