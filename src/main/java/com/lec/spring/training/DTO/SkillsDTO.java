package com.lec.spring.training.DTO;

import com.lec.spring.training.domain.Certification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillsDTO {

    private String skills;

    private MultipartFile img;
    /* DB경로만 저장하지만, DTO는 정보를 받아오는 친구고 우리가 받아올 정보는 이미지.  멀티파트 파일을 받아와야한다.*/

    private Long certificationId;

}// end SkillsDTO
