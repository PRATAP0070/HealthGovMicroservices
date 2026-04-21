package com.healthgov.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.healthgov.dto.DocumentRequestDTO;
import com.healthgov.dto.DocumentResponseDTO;
import com.healthgov.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/document")
public class DocumentController {

    private final DocumentService service;
    
    @PostMapping("/{citizenId}")
    public ResponseEntity<String> upload(@PathVariable Long citizenId, @Valid @RequestBody DocumentRequestDTO request) {
        log.info("Received POST request to upload document for Citizen ID: {}", citizenId);
        service.uploadDocument(citizenId, request);
        return ResponseEntity.ok("Documents posted successfully.");
    }

    @PutMapping("/{citizenId}/{documentId}")
    public ResponseEntity<String> update(@PathVariable Long citizenId, @PathVariable Long documentId, @Valid @RequestBody DocumentRequestDTO request) {
        log.info("Received PUT request for Document ID: {} and Citizen ID: {}", documentId, citizenId);
        String msg = service.updateDocument(citizenId, documentId, request);
        return ResponseEntity.ok(msg);
    }

    @GetMapping("/{citizenId}")
    public ResponseEntity<List<DocumentResponseDTO>> getAll(@PathVariable Long citizenId) {
        log.info("Received GET request for Citizen ID: {}", citizenId);
        return ResponseEntity.ok(service.getCitizenDocuments(citizenId));
    }

    @PutMapping("/{documentId}/verify")
    public ResponseEntity<String> verifyDocument(@PathVariable Long documentId, @RequestParam String status) {
        log.info("Received PUT request to verify Document ID: {} with status: {}", documentId, status);
        String responseMsg = service.verifyDocument(documentId, status);
        return ResponseEntity.ok(responseMsg);
    }
}