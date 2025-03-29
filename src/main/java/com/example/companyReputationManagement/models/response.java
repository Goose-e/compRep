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
public class response extends Company {
    @Column(name = "response_code")
    private String responseCode;
    @Column(name = "feedback_id")
    private Long feedbackId;
    @Column(name = "company_id")
    private Long companyId;
    @Column(name = "response_text")
    private String responseText;
    @Override
    public Long getCoreEntityId() {
        return super.getCoreEntityId(); // Получаем ID из родительского класса
    }

    @Override
    public void setCoreEntityId(Long coreEntityId) {
        super.setCoreEntityId(coreEntityId); // Устанавливаем ID через родительский класс
    }
}
