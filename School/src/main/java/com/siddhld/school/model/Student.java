package com.siddhld.school.model;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Student {
    private String firstName;
    private String lastName;
    @Email(message = "Invalid email")
    private String email;
}