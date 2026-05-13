package com.healthgov.service;

import java.util.List;

import com.healthgov.dto.DocumentRequestDTO;
import com.healthgov.dto.DocumentResponseDTO;

public interface DocumentService {
    
    String uploadDocument(Long citizenId, DocumentRequestDTO request);
    
    List<DocumentResponseDTO> getCitizenDocuments(Long citizenId);
    
    String updateDocument(Long citizenId, Long documentId, DocumentRequestDTO request);
    
    // NEW: Added the delete method signature here so the Controller and Impl can talk!
    void deleteDocument(Long documentId);
    
    String verifyDocument(Long documentId, String status);
    
    List<DocumentResponseDTO> getAllDocumentsForVerification();
}