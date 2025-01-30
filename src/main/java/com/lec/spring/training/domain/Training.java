package com.lec.spring.training.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lec.spring.base.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@DynamicInsert
@Entity(name = "Training")
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "trainerId", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private User trainer;

    @Column
    private Integer times;

    @Column
    @ColumnDefault("0")
    private int total_stamps;

    @Column
    @ColumnDefault("0")
    private int coupons;
}