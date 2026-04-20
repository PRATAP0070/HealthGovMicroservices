package com.healthgov.model;

import java.util.Date;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long enrollmentId;

    private Long citizenId;
    private Long programId;
    private Date date;
    private String status;
}