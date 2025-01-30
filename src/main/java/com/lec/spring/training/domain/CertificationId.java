package com.lec.spring.training.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class CertificationId implements Serializable {

    @Column(name = "id")
    private Long id;

    @Column(name = "trainerProfileId")
    private Long trainerProfileId;
}

