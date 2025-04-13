package com.example.companyReputationManagement.models;

import com.example.companyReputationManagement.models.enums.SentimentTypeEnum;
import com.example.companyReputationManagement.models.enums.converters.SentimentTypeRefConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "review")
public class review extends CoreEntity {
    @Column(name = "review_code")
    private String reviewCode;
    @Column(name = "company_id")
    private Long companyId;
    @Column(name = "source_id")
    private Long sourceId;
    @Column(name = "rating")
    private int rating;
    @Column(name = "content")
    private String content;
    @Column(name = "sentiment_type_id")
    @Convert(converter = SentimentTypeRefConverter.class)
    private SentimentTypeEnum sentimentTypeId;
    @Column(name = "reviewer_name")
    private String reviewerName;
    @Column(name = "published_at")
    private LocalDateTime publishedAt;


}
