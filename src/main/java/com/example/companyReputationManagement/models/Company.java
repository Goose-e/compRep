package com.example.companyReputationManagement.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "company")
@PrimaryKeyJoinColumn(name = "company_id")
public class Company extends CoreEntity {


    @Column(name = "company_code")
    private String companyCode;
    @Column(name = "name")
    private String name;
    @Column(name = "industry")
    private String industry;
    @Column(name = "website")
    private String website;
    @Column(name = "otzovik_url")
    private String otzovikUrl;

}
