package com.siddhld.student.service;

import com.siddhld.student.entity.Student;
import com.siddhld.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService{

    private final StudentRepository repository;

    @Override
    public Student createStudent(Student student) {
        return repository.save(student);
    }

    @Override
    public Optional<Student> getStudentById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    @Override
    public List<Student> getAllBySchoolId(Integer schoolId) {
        return repository.findAllBySchoolId(schoolId);
    }

    @Override
    public Student updateStudent(Integer id, Student studentDetails) {
        return repository.findById(id).map(student -> {

            if(studentDetails.getEmail() != null && !studentDetails.getEmail().isBlank() && !studentDetails.getEmail().isEmpty())
                student.setEmail(studentDetails.getEmail());
            if(studentDetails.getFirstName() != null && !studentDetails.getFirstName().isBlank() && !studentDetails.getFirstName().isEmpty())
                student.setFirstName(studentDetails.getFirstName());
            if(studentDetails.getLastName() != null && !studentDetails.getLastName().isBlank() && !studentDetails.getLastName().isEmpty())
                student.setLastName(studentDetails.getLastName());

            return repository.save(student);
        }).orElseThrow(() -> new RuntimeException("Student not found with id " + id));
    }

    @Override
    public void deleteStudent(Integer id) {
        repository.deleteById(id);
    }
}
