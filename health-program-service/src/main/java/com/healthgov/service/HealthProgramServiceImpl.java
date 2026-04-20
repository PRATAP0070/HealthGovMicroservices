package com.healthgov.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.healthgov.dto.HealthProgramDTO;
import com.healthgov.dto.HealthProgramResponseDTO;
import com.healthgov.dto.ProgramStatusResponse;
import com.healthgov.exceptions.ProgramException;
import com.healthgov.model.HealthProgram;
import com.healthgov.repository.HealthProgramRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HealthProgramServiceImpl implements HealthProgramService {

    private final HealthProgramRepository repo;

    @Override
    public List<HealthProgramResponseDTO> getAllPrograms() {
        return repo.findAll()
                   .stream()
                   .map(this::map)
                   .toList();
    }

    @Override
    public HealthProgramResponseDTO getProgramById(Long id) {
        HealthProgram program = repo.findById(id)
                .orElseThrow(() ->
                        new ProgramException("Program not found", HttpStatus.NOT_FOUND));
        return map(program);
    }

    @Override
    public HealthProgramResponseDTO createProgram(HealthProgramDTO dto) {
        HealthProgram program = new HealthProgram();
        program.setTitle(dto.getTitle());
        program.setDescription(dto.getDescription());
        program.setStartDate(dto.getStartDate());
        program.setEndDate(dto.getEndDate());
        program.setBudget(dto.getBudget());
        program.setStatus(dto.getStatus());

        return map(repo.save(program));
    }

    @Override
    public HealthProgramResponseDTO updateProgram(Long id, HealthProgramDTO dto) {
        HealthProgram program = repo.findById(id)
                .orElseThrow(() ->
                        new ProgramException("Program not found", HttpStatus.NOT_FOUND));

        program.setTitle(dto.getTitle());
        program.setDescription(dto.getDescription());
        program.setStartDate(dto.getStartDate());
        program.setEndDate(dto.getEndDate());
        program.setBudget(dto.getBudget());
        program.setStatus(dto.getStatus());

        return map(repo.save(program));
    }

    @Override
    public void deleteProgram(Long id) {
        if (!repo.existsById(id)) {
            throw new ProgramException("Program not found", HttpStatus.NOT_FOUND);
        }
        repo.deleteById(id);
    }

    @Override
    public Boolean programExists(Long id) {
        return repo.existsById(id);
    }
    
    @Override
    public ProgramStatusResponse getProgramStatus(Long programId) {
 
        HealthProgram program = repo.findById(programId)
                .orElseThrow(() ->
                        new ProgramException("Program not found", HttpStatus.NOT_FOUND));
 
        return new ProgramStatusResponse(
                program.getProgramId(),
                program.getBudget(),
                program.getStatus()
        );
    }

    /* -------------------- Mapping -------------------- */

    private HealthProgramResponseDTO map(HealthProgram program) {
        HealthProgramResponseDTO dto = new HealthProgramResponseDTO();
        dto.setProgramId(program.getProgramId());
        dto.setTitle(program.getTitle());
        dto.setDescription(program.getDescription());
        dto.setStartDate(program.getStartDate());
        dto.setEndDate(program.getEndDate());
        dto.setBudget(program.getBudget());
        dto.setStatus(program.getStatus());
        return dto;
    }
}
