package com.healthgov.model;

import java.time.LocalDate;

import com.healthgov.enums.Gender;
import com.healthgov.enums.RegistrationStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "citizen")
@Data
public class Citizen {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long citizenId;
	private String name;
	private LocalDate dob;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	private String address;
	private String contactInfo;

	@Enumerated(EnumType.STRING)
	private RegistrationStatus status;

	public Object getDocuments() {
		// TODO Auto-generated method stub
		return null;
	}

}
