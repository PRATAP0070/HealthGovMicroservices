package com.healthgov.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.healthgov.dto.DocumentRequestDTO;
import com.healthgov.dto.DocumentResponseDTO;
import com.healthgov.service.DocumentService;

import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Base64;

@Slf4j
@RestController
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping("/{citizenId}")
    public ResponseEntity<List<DocumentResponseDTO>> getDocuments(@PathVariable Long citizenId) {
        log.info("API Call: Fetching documents for citizen ID: {}", citizenId);
        List<DocumentResponseDTO> docs = documentService.getCitizenDocuments(citizenId);
        return ResponseEntity.ok(docs);
    }

    @PostMapping("/{citizenId}")
    public ResponseEntity<String> uploadDocument(
            @PathVariable Long citizenId,
            @RequestParam("documentName") String documentName,
            @RequestParam("documentType") String documentType,
            @RequestParam("file") MultipartFile file) {

        log.info("API Call: Uploading {} document for citizen ID: {}", documentType, citizenId);
        
        // Convert file to Base64
        String fileUrl = saveFileAndReturnUrl(file);
        
        DocumentRequestDTO dto = new DocumentRequestDTO();
        dto.setDocumentName(documentName);
        dto.setDocumentType(documentType);
        dto.setFileUrl(fileUrl);
        
        documentService.uploadDocument(citizenId, dto);
        return ResponseEntity.ok("Document uploaded successfully");
    }

    // --- RESTORED ENDPOINTS BELOW ---

    @PutMapping("/{citizenId}/{documentId}")
    public ResponseEntity<String> updateDocument(
            @PathVariable Long citizenId,
            @PathVariable Long documentId,
            @RequestBody DocumentRequestDTO request) {
        
        log.info("API Call: Updating document {} for citizen ID: {}", documentId, citizenId);
        return ResponseEntity.ok(documentService.updateDocument(citizenId, documentId, request));
    }

    @DeleteMapping("/{citizenId}/{documentId}")
    public ResponseEntity<String> deleteDocument(
            @PathVariable Long citizenId,
            @PathVariable Long documentId) {
        
        log.info("API Call: Deleting document {} for citizen ID: {}", documentId, citizenId);
        documentService.deleteDocument(documentId);
        return ResponseEntity.ok("Document deleted successfully");
    }

    // THE MISSING VERIFY ENDPOINT THAT CAUSED YOUR ERROR
    @PutMapping("/{documentId}/verify")
    public ResponseEntity<String> verifyDocument(
            @PathVariable Long documentId, 
            @RequestParam String status) {
        
        log.info("API Call: Verifying document {}", documentId);
        return ResponseEntity.ok(documentService.verifyDocument(documentId, status));
    }

    // ---------------------------------

    private String saveFileAndReturnUrl(MultipartFile file) {
        try {
            // This creates a Data URL that the browser understands as an image/file
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            return "data:" + file.getContentType() + ";base64," + base64Image;
        } catch (Exception e) {
            log.error("Failed to process document file", e);
            throw new RuntimeException("Failed to process document file");
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<DocumentResponseDTO>> getAllDocuments() {
        log.info("API Call: Fetching all documents for verification");
        return ResponseEntity.ok(documentService.getAllDocumentsForVerification());
    }
}