package com.lec.spring.training.repository;

import com.lec.spring.training.DTO.TrainerProfileDTO;
import com.lec.spring.training.domain.TrainerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrainerProfileRepository extends JpaRepository<TrainerProfile, Long> {

    TrainerProfile findByTrainerId(Long id);

    @Query("SELECT new com.lec.spring.training.DTO.TrainerProfileDTO(tp.id, tp.perPrice, tp.content, tp.career, null, null) " +
            "FROM TrainerProfile tp WHERE tp.id = :id")
    TrainerProfileDTO findTrainerProfileDTOById(@Param("id") Long id);


}
