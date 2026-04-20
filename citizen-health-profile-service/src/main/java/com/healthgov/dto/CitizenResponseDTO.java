package com.healthgov.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitizenResponseDTO {

	private Long userId;
    private Long citizenId;
    private String name;
    private LocalDate dob;
    private String gender;
    private String address;
    private String status;

}