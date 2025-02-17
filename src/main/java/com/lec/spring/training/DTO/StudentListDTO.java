package com.lec.spring.training.DTO;

import com.lec.spring.training.DTO.output.StudentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentListDTO {
    private Long userId;
    private String nickname;
    private String email;
    private String profileImage;
    private int times;
    private int trainingId; //hjy
    private List<StudentDTO> students;
}
