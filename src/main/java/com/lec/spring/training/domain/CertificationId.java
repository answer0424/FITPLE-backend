package com.lec.spring.training.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Data
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class CertificationId implements Serializable {

    @Column(name = "id")
    private Long id;

    @Column(name =  "trainer_profile_id")
    private Long trainerProfileId;


}

