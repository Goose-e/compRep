package com.example.companyReputationManagement.models;

import com.example.companyReputationManagement.models.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "company")
public class Company extends CoreEntity  {


    @Column(name = "company_code")
    private String companyCode;
    @Column(name = "name")
    private String name;
    @Column(name = "industry")
    private String industry;
    @Column(name = "website")
    private String website;





}
