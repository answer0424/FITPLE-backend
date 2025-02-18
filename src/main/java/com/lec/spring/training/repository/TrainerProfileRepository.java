package com.lec.spring.training.repository;

import com.lec.spring.base.domain.User;
import com.lec.spring.training.DTO.TrainerProfileDTO;
import com.lec.spring.training.domain.GrantStatus;
import com.lec.spring.training.DTO.TrainerProfileReadDTO;
import com.lec.spring.training.domain.TrainerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerProfileRepository extends JpaRepository<TrainerProfile, Long> {

    @Query("SELECT new com.lec.spring.training.DTO.TrainerProfileDTO(tp.id, tp.perPrice, tp.content, tp.career, null, null) " +
            "FROM TrainerProfile tp WHERE tp.id = :id")
    TrainerProfileDTO findTrainerProfileDTOById(@Param("id") Long id);

    Optional<TrainerProfile> findByTrainerId(Long trainerId);

    List<TrainerProfile> findByIsAccess(GrantStatus isAccess);

    @Query("SELECT DISTINCT tp FROM TrainerProfile tp " +
            "JOIN FETCH tp.trainer t " +
            "LEFT JOIN FETCH t.hbti h " +  // OneToOne 관계를 활용한 직접 조인
            "JOIN FETCH t.gym g " +
            "WHERE g.address LIKE %:district% " +
            "AND h.hbti IN :hbtiList " +
            "AND tp.isAccess = '승인'")
    List<TrainerProfile> findMatchingTrainersWithFetch(
            @Param("district") String district,
            @Param("hbtiList") List<String> hbtiList);

    Optional<TrainerProfile> findByTrainer(User username);
}








