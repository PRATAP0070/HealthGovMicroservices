package com.healthgov.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthgov.dto.EnrollmentDTO;
import com.healthgov.service.EnrollmentService;

import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService service; // Use Interface here

    @PostMapping("/create")
    public EnrollmentDTO create(@RequestBody EnrollmentDTO dto) throws Exception {
        return service.createEnrollment(dto);
    }

    @GetMapping("/all")
    public List<EnrollmentDTO> all() {
        return service.getAllEnrollments();
    }
    @GetMapping("/{id}")
    public EnrollmentDTO getById(@PathVariable Long id) {
        return service.getEnrollmentById(id);
    }
    
    @PutMapping("/update")
    public EnrollmentDTO update( @RequestBody EnrollmentDTO dto) {
        return service.updateEnrollment(dto);
    }
    
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteEnrollment(id);
        return "Deleted Enrollment "+id;
    }
    
    
    
    
}