package com.siddhld.school.service;

import com.siddhld.school.entity.School;
import com.siddhld.school.model.FullSchoolResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

public interface SchoolService {
    School createSchool(School school);
    Optional<School> getSchoolById(Integer id);
    Optional<FullSchoolResponse> getSchoolWithStudent(Integer schoolId);
    List<School> getAllSchools();
    School updateSchool(Integer id, School schoolDetails);
    void deleteSchool(Integer id);
}
