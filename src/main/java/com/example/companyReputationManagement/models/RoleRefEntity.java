package com.example.companyReputationManagement.models;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "role_ref")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRefEntity {
    @Id
    Long role_ref_id;
    String role_code;
    String role_name;
}
