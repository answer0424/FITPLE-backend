package com.lec.spring.base.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "HBTI")
@Data
public class HBTI {
    @Id
    private Long userId;

    @OneToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonBackReference // 순환 참조 방지
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
