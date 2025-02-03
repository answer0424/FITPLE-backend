package com.lec.spring.base.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "HBTI")
@Data
public class HBTI {
    @Id
    private Long userId;

    @OneToOne
    @JoinColumn(name = "UserId", nullable = false)
    private User user;

    @Column(nullable = false, length = 4)
    private String hbti;

    @Column(nullable = false)
    private Double mbScore;

    @Column(nullable = false)
    private Double eiScore;

    @Column(nullable = false)
    private Double cnScore;

    @Column(nullable = false)
    private Double pgScore;
}
