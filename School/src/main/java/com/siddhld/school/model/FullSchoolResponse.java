package com.siddhld.school.model;

import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FullSchoolResponse {
    private String name;
    @Email(message = "Invalid email")
    private String email;
    List<Student> students;
}