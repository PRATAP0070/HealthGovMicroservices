package com.healthgov.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthgov.dto.DocumentRequestDTO;
import com.healthgov.dto.DocumentResponseDTO;
import com.healthgov.enums.DocumentType;
import com.healthgov.enums.VerificationStatus;
import com.healthgov.exceptions.CitizenNotFoundException;
import com.healthgov.model.Citizen;
import com.healthgov.model.CitizenDocument;
import com.healthgov.repository.CitizenRepository;
import com.healthgov.repository.DocumentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepo;
    private final CitizenRepository citizenRepo; 

    @Override
    public String uploadDocument(Long citizenId, DocumentRequestDTO request) {
        log.info("Request to upload {} for Citizen ID: {}", request.getDocumentType(), citizenId);
        
        Citizen citizen = citizenRepo.findById(citizenId)
                .orElseThrow(() -> new CitizenNotFoundException(citizenId));

        CitizenDocument doc = new CitizenDocument();
        doc.setCitizen(citizen); 
        doc.setDocumentName(request.getDocumentName());
        
        try {
            doc.setDocType(DocumentType.valueOf(request.getDocumentType().trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid document type. Use IDPROOF or HEALTHCARD.");
        }
        
        doc.setFileURI(request.getFileUrl());
        doc.setUploadedDate(LocalDateTime.now());
        doc.setVerificationStatus(VerificationStatus.PENDING); 

        documentRepo.save(doc);
        log.info("Successfully saved document '{}' for Citizen {}", request.getDocumentName(), citizenId);
        return "Document uploaded successfully.";
    }

    @Override
    public List<DocumentResponseDTO> getCitizenDocuments(Long citizenId) {
        log.info("Fetching documents for Citizen ID: {}", citizenId);
        List<CitizenDocument> allDocuments = documentRepo.findAll();
        List<DocumentResponseDTO> filteredResults = new ArrayList<>();

        for (CitizenDocument doc : allDocuments) {
            if (doc.getCitizen() != null && doc.getCitizen().getCitizenId().equals(citizenId)) {
                filteredResults.add(mapToDTO(doc));
            }
        }
        return filteredResults;
    }

    @Override
    public String updateDocument(Long citizenId, Long documentId, DocumentRequestDTO request) {
        log.info("Updating Document ID: {} for Citizen: {}", documentId, citizenId);

        CitizenDocument doc = documentRepo.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found."));

        if (!doc.getCitizen().getCitizenId().equals(citizenId)) {
            throw new RuntimeException("Unauthorized: Document does not belong to this citizen.");
        }

        doc.setDocumentName(request.getDocumentName());
        
        try {
            doc.setDocType(DocumentType.valueOf(request.getDocumentType().trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid document type. Use IDPROOF or HEALTHCARD.");
        }
        
        doc.setFileURI(request.getFileUrl());
        doc.setUploadedDate(LocalDateTime.now());
        doc.setVerificationStatus(VerificationStatus.PENDING);

        documentRepo.save(doc);
        return "Document updated successfully.";
    }

    @Override
    public String verifyDocument(Long documentId, String status) {
        log.info("Request to verify Document ID: {} with status: {}", documentId, status);
        
        CitizenDocument doc = documentRepo.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document ID " + documentId + " not found."));

        try {
            doc.setVerificationStatus(VerificationStatus.valueOf(status.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid verification status. Please use a valid VerificationStatus.");
        }

        documentRepo.save(doc);
        log.info("Document ID: {} verification status updated to {}", documentId, status);
        return "Document verification status updated to " + status;
    }

    private DocumentResponseDTO mapToDTO(CitizenDocument d) {
        return new DocumentResponseDTO(
                d.getDocumentId(), 
                d.getDocumentName(), 
                d.getDocType().name(),
                d.getFileURI(), 
                d.getUploadedDate(), 
                d.getCitizen().getCitizenId(), 
                d.getVerificationStatus() != null ? d.getVerificationStatus().name() : "PENDING"
        );
    }
}