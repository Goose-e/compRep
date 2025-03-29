package com.example.companyReputationManagement.models;

import com.example.companyReputationManagement.models.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "company")
public class Company extends CoreEntity {

    @Column(name = "company_code")
    private String companyCode;
    @Column(name = "name")
    private String name;
    @Column(name = "industry")
    private String industry;
    @Column(name = "website")
    private String website;


    @Override
    public Long getCoreEntityId() {
        return super.getCoreEntityId(); // Получаем ID из родительского класса
    }

    @Override
    public void setCoreEntityId(Long coreEntityId) {
        super.setCoreEntityId(coreEntityId); // Устанавливаем ID через родительский класс
    }


}
