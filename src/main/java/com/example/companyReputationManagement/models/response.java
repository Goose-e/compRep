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
@Table(name = "response")
public class response extends CoreEntity {
    @Column(name = "response_code")
    private String responseCode;
    @Column(name = "feedback_id")
    private Long feedbackId;
    @Column(name = "company_id")
    private Long companyId;
    @Column(name = "response_text")
    private String responseText;

}
