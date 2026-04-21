package com.healthgov.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class AduitLog {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;
    private Long userId;
 
    private String action;
    private String resource;
    private Date timestamp;
}
