package com.healthgov.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        log.info("Attempting to upload {} for Citizen ID: {}", request.getDocumentType(), citizenId);

        Citizen citizen = citizenRepo.findById(citizenId)
                .orElseThrow(() -> new CitizenNotFoundException("Citizen not found with ID: " + citizenId));

        CitizenDocument doc = new CitizenDocument();
        doc.setCitizen(citizen);
        doc.setDocumentName(request.getDocumentName());
        
        doc.setFileUrl(request.getFileUrl());
        
        doc.setUploadedDate(LocalDateTime.now());
        doc.setVerificationStatus(VerificationStatus.PENDING);

        try {
            doc.setDocType(DocumentType.valueOf(request.getDocumentType().trim().toUpperCase()));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid document type. Use IDPROOF or HEALTHCARD.");
        }

        documentRepo.save(doc);
        return "Document uploaded successfully.";
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponseDTO> getCitizenDocuments(Long citizenId) {
        log.info("Fetching documents for Citizen ID: {}", citizenId);
        return documentRepo.findAll().stream()
                .filter(doc -> doc.getCitizen() != null && doc.getCitizen().getCitizenId().equals(citizenId))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String updateDocument(Long citizenId, Long documentId, DocumentRequestDTO request) {
        CitizenDocument doc = documentRepo.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found."));

        if (!doc.getCitizen().getCitizenId().equals(citizenId)) {
            throw new RuntimeException("Unauthorized: Document does not belong to this citizen.");
        }

        doc.setDocumentName(request.getDocumentName());
        
        doc.setFileUrl(request.getFileUrl());
        
        doc.setUploadedDate(LocalDateTime.now());
        doc.setVerificationStatus(VerificationStatus.PENDING);

        try {
            doc.setDocType(DocumentType.valueOf(request.getDocumentType().trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid document type.");
        }

        documentRepo.save(doc);
        return "Document updated successfully.";
    }

    @Override
    public String verifyDocument(Long documentId, String status) {
        CitizenDocument doc = documentRepo.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document ID " + documentId + " not found."));

        try {
            doc.setVerificationStatus(VerificationStatus.valueOf(status.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status. Use PENDING, VERIFIED, REJECTED, or EXPIRED.");
        }

        documentRepo.save(doc);
        return "Document verification status updated to " + status;
    }

    // --- NEW: DELETE DOCUMENT METHOD ADDED HERE ---
    @Override
    public void deleteDocument(Long documentId) {
        log.info("Deleting document with ID: {}", documentId);
        if (!documentRepo.existsById(documentId)) {
            throw new RuntimeException("Document not found.");
        }
        documentRepo.deleteById(documentId);
    }
    // ----------------------------------------------

    private DocumentResponseDTO mapToDTO(CitizenDocument d) {
        return new DocumentResponseDTO(
                d.getDocumentId(),
                d.getDocumentName(),
                (d.getDocType() != null) ? d.getDocType().name() : null,
                d.getFileUrl(),
                d.getUploadedDate(),
                (d.getCitizen() != null) ? d.getCitizen().getCitizenId() : null,
                (d.getVerificationStatus() != null) ? d.getVerificationStatus().name() : "PENDING"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponseDTO> getAllDocumentsForVerification() {
        log.info("Fetching all documents in the system for provider verification");
        
        // 1. Fetch all documents from the repository
        List<CitizenDocument> allDocs = documentRepo.findAll();
        
        // 2. Stream through the list and map each entity to a DTO
        // We reuse your existing mapToDTO helper method for consistency
        return allDocs.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}