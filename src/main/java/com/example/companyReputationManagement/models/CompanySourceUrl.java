package com.example.companyReputationManagement.models;

import com.example.companyReputationManagement.models.enums.SourcesEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "company_source_url")
@PrimaryKeyJoinColumn(name = "company_source_url_id")
public class CompanySourceUrl extends CoreEntity {
    @Column(name = "company_source_url_code")
    private String companySourceUrlCode;
    @Column(name = "company_id")
    private Long companyId;
    @Column(name = "source_url")
    private String sourceUrl;
    @Column(name = "source_type")
    private SourcesEnum sourceType;
}
