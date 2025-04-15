package com.example.companyReputationManagement.models;

import com.example.companyReputationManagement.models.enums.RoleEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_company_roles")
@PrimaryKeyJoinColumn(name = "user_company_roles_id")
public class UserCompanyRoles extends CoreEntity {
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "company_id")
    private Long companyId;
    @Column(name = "role")
    private RoleEnum role;
}
