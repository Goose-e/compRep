package com.example.companyReputationManagement.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "status_ref")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusRefEntity {

    @Id
    private Long status_id;

    private String status_code;

    private String status_name;
}
