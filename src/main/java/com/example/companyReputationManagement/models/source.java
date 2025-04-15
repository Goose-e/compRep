package com.example.companyReputationManagement.models;

import com.example.companyReputationManagement.models.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "source")
@PrimaryKeyJoinColumn(name = "source_id")
public class source extends CoreEntity {
    @Column(name = "source_code")
    private String sourceCode;
    @Column(name = "name")
    private String name;
    @Column(name = "url")
    private String url;


}
