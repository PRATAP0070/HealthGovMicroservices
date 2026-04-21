package com.healthgov.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitizenResponseDTO {
    private Long userId;     // Order 1
    private Long citizenId;  // Order 2
    private String name;     // Order 3
    private LocalDate dob;   // Order 4
    private String gender;   // Order 5
    private String address;  // Order 6
    private String status;   // Order 7
}