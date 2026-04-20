package com.healthgov.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponseDTO {

    private Long documentId;
    private String documentName;
    private String documentType;
    private String fileUrl;
    private LocalDateTime uploadedAt;
    private Long citizenId;
    private String verificationStatus;

}