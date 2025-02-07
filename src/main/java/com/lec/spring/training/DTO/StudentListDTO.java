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

    private int times;
    private List<StudentDTO> students;
}
