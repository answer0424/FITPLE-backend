package com.lec.spring.training.repository;

import com.lec.spring.training.DTO.TrainerProfileDTO;
import com.lec.spring.training.domain.GrantStatus;
import com.lec.spring.training.DTO.TrainerProfileReadDTO;
import com.lec.spring.training.domain.TrainerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainerProfileRepository extends JpaRepository<TrainerProfile, Long> {

    @Query("SELECT new com.lec.spring.training.DTO.TrainerProfileDTO(tp.id, tp.perPrice, tp.content, tp.career, null, null) " +
            "FROM TrainerProfile tp WHERE tp.id = :id")
    TrainerProfileDTO findTrainerProfileDTOById(@Param("id") Long id);

    Optional<TrainerProfile> findByTrainerId(Long trainerId);

    List<TrainerProfile> findByIsAccess(GrantStatus isAccess);

    @Query("SELECT DISTINCT tp FROM TrainerProfile tp " +
            "JOIN FETCH tp.trainer t " +
            "JOIN FETCH t.gym g " +
            "JOIN HBTI h ON h.user = t " +  // HBTI 테이블과 조인
            "WHERE g.address LIKE %:district% " +
            "AND h.hbti IN :hbtiList " +    // HBTI 엔티티의 hbti 필드 사용
            "AND tp.isAccess = '승인'")
    List<TrainerProfile> findMatchingTrainersWithFetch(
            @Param("district") String district,
            @Param("hbtiList") List<String> hbtiList);
}