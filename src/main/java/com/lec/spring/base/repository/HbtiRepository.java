package com.lec.spring.base.repository;

import com.lec.spring.base.domain.HBTI;
import com.lec.spring.base.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HbtiRepository extends JpaRepository<HBTI, Long> {

    @Query("SELECT h.user FROM HBTI h WHERE h.hbti = :hbtiType")
    List<User> findUsersByHbtiType(@Param("hbti") String hbtiType);

    Optional<HBTI> findByUserId(Long userId);

    Optional<HBTI> findByUser_Id(Long userId);


}
