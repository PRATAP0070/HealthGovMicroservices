package com.healthgov.model;

import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.healthgov.enums.HealthProfileStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "health_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "medical_history_json", columnDefinition = "json")
    private Map<String, Object> medicalHistoryJson; 

    private String allergies;

    @Enumerated(EnumType.STRING) 
    @Column(name = "status", length = 20)
    private HealthProfileStatus status;
    
    @OneToOne
    @JoinColumn(name = "citizen_id", nullable = false)
    private Citizen citizen;

}