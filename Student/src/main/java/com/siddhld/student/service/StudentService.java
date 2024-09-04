package com.siddhld.student.service;

import com.siddhld.student.entity.Student;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    Student createStudent(Student student);
    Optional<Student> getStudentById(Integer id);
    List<Student> getAllStudents();
    List<Student> getAllBySchoolId(Integer schoolId);
    Student updateStudent(Integer id, Student studentDetails);
    void deleteStudent(Integer id);
}
