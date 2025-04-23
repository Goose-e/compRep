package com.example.companyReputationManagement.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "response")
@PrimaryKeyJoinColumn(name = "response_id")
public class Response extends CoreEntity {
    @Column(name = "response_code")
    private String responseCode;
    @Column(name = "feedback_id")
    private Long feedbackId;
    @Column(name = "company_id")
    private Long companyId;
    @Column(name = "response_text")
    private String responseText;

}
