package com.example.companyReputationManagement.models;

import com.example.companyReputationManagement.models.enums.SentimentTypeRef;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "feedback")
public class Feedback extends CoreEntity {
    @Column(name = "feedback_code")
    private String feedbackCode;
    @Column(name = "company_id")
    private Long companyId;
    @Column(name = "source_id")
    private Long sourceId;
    @Column(name = "type")
    private String type;
    @Column(name = "rating")
    private int rating;
    @Column(name = "content")
    private String content;
    @Column(name = "sentiment_type_id")
    private Long sentimentTypeId;
    @Column(name = "published_at")
    private LocalDateTime publishedAt;


    @Override
    public Long getCoreEntityId() {
        return super.getCoreEntityId(); // Получаем ID из родительского класса
    }

    @Override
    public void setCoreEntityId(Long coreEntityId) {
        super.setCoreEntityId(coreEntityId); // Устанавливаем ID через родительский класс
    }
}
