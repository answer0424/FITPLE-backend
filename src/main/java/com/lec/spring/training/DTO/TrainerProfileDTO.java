package com.lec.spring.training.DTO;

import com.lec.spring.base.domain.User;
import com.lec.spring.training.domain.Certification;
import com.lec.spring.training.domain.GrantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainerProfileDTO {
    private Long trainerId;

    private Integer perPrice;

    private String content;

    private Date career;

//    private List<Certification> certificationList = new ArrayList<>();
    // List<MultipartFile> <- TrainerDetailService에서 매개변수로 받기 때문에

//    private List<SkillsDTO> skills;

    private Long[] deletedSkillsId;


}// end TrainerProfileDTO

