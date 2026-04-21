package com.healthgov.service;

import java.util.List;
import com.healthgov.dto.DocumentRequestDTO;
import com.healthgov.dto.DocumentResponseDTO;

public interface DocumentService {
    String uploadDocument(Long citizenId, DocumentRequestDTO request);
    List<DocumentResponseDTO> getCitizenDocuments(Long citizenId);
    String updateDocument(Long citizenId, Long documentId, DocumentRequestDTO request);
    
    String verifyDocument(Long documentId, String status);
}