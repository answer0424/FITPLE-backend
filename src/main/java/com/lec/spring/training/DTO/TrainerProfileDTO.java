package com.lec.spring.training.DTO;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainerProfileDTO {
    private Long trainerId;

    private Integer perPrice;

    private String content;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate career;

    private Long[] deletedSkillsId;

   private List<SkillsDTO> certificationSkills = new ArrayList<>();


}// end TrainerProfileDTO