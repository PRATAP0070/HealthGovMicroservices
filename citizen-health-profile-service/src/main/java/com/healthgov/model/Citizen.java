package com.healthgov.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.healthgov.enums.Gender;
import com.healthgov.enums.RegistrationStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "citizen")
@Data
public class Citizen {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long citizenId;

    private Long userId; 

    private String name;
    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String address;
    private String contactInfo;

    @Enumerated(EnumType.STRING)
    private RegistrationStatus status;

    @OneToOne(mappedBy = "citizen", cascade = CascadeType.ALL, orphanRemoval = true)
    private HealthProfile healthProfile;

    @OneToMany(mappedBy = "citizen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CitizenDocument> documents = new ArrayList<>();
}