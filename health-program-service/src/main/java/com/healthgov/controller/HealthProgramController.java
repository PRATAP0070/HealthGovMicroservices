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

import com.healthgov.dto.HealthProgramDTO;
import com.healthgov.dto.HealthProgramResponseDTO;
import com.healthgov.dto.ProgramStatusResponse;
import com.healthgov.service.HealthProgramService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/programs")
public class HealthProgramController {

    private final HealthProgramService service;

    public HealthProgramController(HealthProgramService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public List<HealthProgramResponseDTO> getAll() {
        return service.getAllPrograms();
    }

    @GetMapping("/{id}/exists")
    public Boolean programExists(@PathVariable Long id) {
        return service.programExists(id);
    }

    @GetMapping("/{id}")
    public HealthProgramResponseDTO getById(@PathVariable Long id) {
        return service.getProgramById(id);
    }

    @PostMapping("/create")
    public HealthProgramResponseDTO create(@RequestBody HealthProgramDTO dto, HttpServletRequest request) {
        return service.createProgram(dto,request); 
    }

    @PutMapping("/{id}")
    public HealthProgramResponseDTO update(
            @PathVariable Long id,
            @RequestBody HealthProgramDTO dto) {
        return service.updateProgram(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteProgram(id);
    }
    
    @GetMapping("/program-status/{programId}")
    public ProgramStatusResponse getProgramStatus(@PathVariable Long programId) {
        return service.getProgramStatus(programId);
    }
}