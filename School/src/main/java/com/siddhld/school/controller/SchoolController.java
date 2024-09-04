package com.siddhld.school.controller;

import com.siddhld.school.entity.School;
import com.siddhld.school.model.FullSchoolResponse;
import com.siddhld.school.service.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schools")
public class SchoolController {

    private final SchoolService service;

    @PostMapping
    public ResponseEntity<School> createSchool(@RequestBody School school) {
        School createdSchool = service.createSchool(school);
        return ResponseEntity.ok(createdSchool);
    }

    @GetMapping("/{id}")
    public ResponseEntity<School> getSchoolById(@PathVariable Integer id) {
        Optional<School> school = service.getSchoolById(id);
        return school.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/school-with-students/{schoolId}")
    public ResponseEntity<FullSchoolResponse> getSchoolWithStudent(@PathVariable("schoolId") Integer schoolId) {
        Optional<FullSchoolResponse> school = service.getSchoolWithStudent(schoolId);
        return school.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<School>> getAllSchools() {
        List<School> schools = service.getAllSchools();
        return ResponseEntity.ok(schools);
    }

    @PutMapping("/{id}")
    public ResponseEntity<School> updateSchool(@PathVariable Integer id, @RequestBody School schoolDetails) {
        try {
            School updatedSchool = service.updateSchool(id, schoolDetails);
            return ResponseEntity.ok(updatedSchool);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchool(@PathVariable Integer id) {
        try {
            service.deleteSchool(id);
            return ResponseEntity.ok("Deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
