package com.healthgov.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitizenRequestDTO {

    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @NotNull
    @Past
    private LocalDate dob;

    @NotBlank
    @Pattern(regexp = "(?i)^(MALE|FEMALE|OTHER)$")
    private String gender;

    @NotBlank
    @Size(max = 250)
    private String address;

    @NotBlank
    @Size(max = 100)
    private String contactInfo;
}