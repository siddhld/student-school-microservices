package com.siddhld.student.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Student {

    @Id
    private Integer id;
    private String firstName;
    private String lastName;
    @Email(message = "Invalid email")
    private String email;
    private Integer schoolId;
}
