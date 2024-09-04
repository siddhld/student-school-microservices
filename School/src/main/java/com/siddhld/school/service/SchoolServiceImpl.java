package com.siddhld.school.service;

import com.siddhld.school.client.StudentClient;
import com.siddhld.school.entity.School;
import com.siddhld.school.model.FullSchoolResponse;
import com.siddhld.school.model.Student;
import com.siddhld.school.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SchoolServiceImpl implements SchoolService {

    private final SchoolRepository repository;
    private final StudentClient studentClient;

    @Override
    public School createSchool(School school) {
        return repository.save(school);
    }

    @Override
    public Optional<School> getSchoolById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public Optional<FullSchoolResponse> getSchoolWithStudent(Integer schoolId) {
        School school = repository.findById(schoolId)
                .orElse(
                        School.builder()
                                .name("NOT_FOUND")
                                .email("NOT_FOUND")
                                .build());

        // Fetching All Students with school-id using Feign Client
        List<Student> students = studentClient.findAllStudentsBySchool(schoolId);

        return Optional.ofNullable(
                FullSchoolResponse.builder()
                        .name(school.getName())
                        .email(school.getEmail())
                        .students(students)
                .build());
    }

    @Override
    public List<School> getAllSchools() {
        return repository.findAll();
    }

    @Override
    public School updateSchool(Integer id, School schoolDetails) {
        return repository.findById(id).map(school -> {

            if(schoolDetails.getName() != null && !schoolDetails.getName().isBlank() && !schoolDetails.getName().isEmpty())
                school.setName(schoolDetails.getName());
            if(schoolDetails.getEmail() != null && !schoolDetails.getEmail().isBlank() && !schoolDetails.getEmail().isEmpty())
                school.setEmail(schoolDetails.getEmail());

            return repository.save(school);
        }).orElseThrow(() -> new RuntimeException("School not found with id " + id));
    }

    @Override
    public void deleteSchool(Integer id) {
        repository.deleteById(id);
    }
}
