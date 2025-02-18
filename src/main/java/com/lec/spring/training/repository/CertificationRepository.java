package com.lec.spring.training.repository;

import com.lec.spring.training.domain.Certification;
import com.lec.spring.training.domain.CertificationId;
import com.lec.spring.training.domain.TrainerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, CertificationId> {


        @Modifying
        @Transactional
        @Query("DELETE FROM Certification c WHERE c.id IN ?1")
        void deleteAllByIdInBatch(List<Long> ids);

        @Modifying
        @Transactional
        @Query("DELETE FROM Certification c WHERE c.id.trainerProfileId = :trainerProfileId AND c.id.id IN :certificationIds")
        void deleteCertifications(@Param("trainerProfileId") Long trainerProfileId,
                                  @Param("certificationIds") List<Long> certificationIds);


        @Query("SELECT c.credentials FROM Certification c WHERE c.trainerProfile.id = :trainerProfileId")
                List<String> findCredentialsByTrainerProfileId(Long trainerProfileId);


}// end CertificationRepository
