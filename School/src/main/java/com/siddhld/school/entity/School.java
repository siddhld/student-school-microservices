package com.siddhld.school.entity;

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
public class School {

    @Id
    private Integer id;
    private String name;
    @Email(message = "Invalid email")
    private String email;
}
