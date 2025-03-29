package com.example.companyReputationManagement.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "company_user")
public class CompanyUser extends CoreEntity {
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "company_id")
    private Long companyId;
    @Column(name = "role_ref_id")
    private Long roleRefId;
    @Column(name = "username")
    private String username;
    @Column(name = "email")
    private String email;
    @Column(name = "password_hash")
    private String passwordHash;

    @Override
    public Long getCoreEntityId() {
        return super.getCoreEntityId(); // Получаем ID из родительского класса
    }

    @Override
    public void setCoreEntityId(Long coreEntityId) {
        super.setCoreEntityId(coreEntityId); // Устанавливаем ID через родительский класс
    }
}
