package com.example.companyReputationManagement.models;

import com.example.companyReputationManagement.models.enums.SentimentTypeEnum;
import com.example.companyReputationManagement.models.enums.converters.SentimentTypeRefConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "feedback")
@PrimaryKeyJoinColumn(name = "feedback_id")
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
    @Convert(converter = SentimentTypeRefConverter.class)
    private SentimentTypeEnum sentimentTypeId;
    @Column(name = "published_at")
    private LocalDateTime publishedAt;


}
