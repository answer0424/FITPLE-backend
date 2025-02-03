package com.lec.spring.training.repository;

import com.lec.spring.training.DTO.MonthReservationDTO;
import com.lec.spring.training.DTO.TodayReservationDTO;
import com.lec.spring.training.domain.Reservation;
import com.lec.spring.training.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByTrainingId(Long trainingId);

    List<Reservation> findByStatus(ReservationStatus status);

    @Query("SELECT new com.lec.spring.training.DTO.MonthReservationDTO(r.id, u.id, " +
            "CASE WHEN u.authority = 'ROLE_TRAINER' THEN u.nickname ELSE tr.nickname END, r.date) " +
            "FROM Reservation r JOIN r.training t JOIN t.user u JOIN t.trainer tr " +
            "WHERE u.id = :userId AND FUNCTION('YEAR', r.date) = :year AND FUNCTION('MONTH', r.date) = :month")
    List<MonthReservationDTO> findReservationsByUserAndMonth(@Param("userId") Long userId,
                                                             @Param("year") int year,
                                                             @Param("month") int month);

    @Query("SELECT new com.lec.spring.training.DTO.TodayReservationDTO(u.nickname, " +
            "CAST(r.status AS string), u.profileImage, r.date) " +
            "FROM Reservation r " +
            "JOIN r.training t " +
            "JOIN t.user u " +
            "WHERE u.id = :userId AND r.date = :date")
    List<TodayReservationDTO> findTodayReservationsByUser(@Param("userId") Long userId,
                                                          @Param("date") LocalDateTime date);

}// end ReservationRepository
