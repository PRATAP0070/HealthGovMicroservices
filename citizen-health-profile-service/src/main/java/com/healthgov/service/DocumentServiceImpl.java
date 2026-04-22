package com.healthgov.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        doc.setFileURI(request.getFileUrl());
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
        doc.setFileURI(request.getFileUrl());
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

    private DocumentResponseDTO mapToDTO(CitizenDocument d) {
        // Null-safe mapping to prevent 500 errors during GET requests
        return new DocumentResponseDTO(
                d.getDocumentId(),
                d.getDocumentName(),
                (d.getDocType() != null) ? d.getDocType().name() : null,
                d.getFileURI(),
                d.getUploadedDate(),
                (d.getCitizen() != null) ? d.getCitizen().getCitizenId() : null,
                (d.getVerificationStatus() != null) ? d.getVerificationStatus().name() : "PENDING"
        );
    }
}