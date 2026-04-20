package com.healthgov.service;

import com.healthgov.dto.EnrollmentDTO;
import java.util.List;

public interface EnrollmentService {
    List<EnrollmentDTO> getAllEnrollments();
    EnrollmentDTO getEnrollmentById(Long id);
    EnrollmentDTO createEnrollment(EnrollmentDTO dto) throws Exception;
    EnrollmentDTO updateEnrollment(EnrollmentDTO dto);
    void deleteEnrollment(Long id);
}