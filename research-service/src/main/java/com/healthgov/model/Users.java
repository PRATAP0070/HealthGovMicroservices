package com.healthgov.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.healthgov.enums.Role;

import java.util.List;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Users {
	@Id
	//@Min(value = 2500001)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(nullable = false, length = 100)
	private String name;

    @Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Role role;

	@Column(nullable = false, unique = true, length = 120)
	private String email;

	@Column(nullable = false, unique = true, length = 15)
	private String phone;

	@Column(nullable = false, length = 20)
	private String status;
	
	//@Min(value = 8)
	@Column(nullable = false)
	@JsonIgnore
	private String password;

	@OneToMany(mappedBy = "user")
	@JsonIgnore
	private List<AuditLog> auditLogs;

	@OneToMany(mappedBy = "user")
	@JsonIgnore
	private List<Notification> notifications;
}
