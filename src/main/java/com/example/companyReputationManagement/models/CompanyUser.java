package com.example.companyReputationManagement.models;


import com.example.companyReputationManagement.models.enums.RoleEnum;
import com.example.companyReputationManagement.models.enums.converters.RoleEnumConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "company_user")
@PrimaryKeyJoinColumn(name = "user_id")
public class CompanyUser extends CoreEntity {
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "role_ref_id")
    @Convert(converter = RoleEnumConverter.class)
    private RoleEnum roleRefId;
    @Column(name = "username")
    private String username;
    @Column(name = "email")
    private String email;
    @Column(name = "password_hash")
    private String passwordHash;

}
