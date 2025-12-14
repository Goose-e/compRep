package com.example.companyReputationManagement.models;

import com.example.companyReputationManagement.dto.review.keyWord.bot.BotResponseDTO;
import com.example.companyReputationManagement.models.enums.SentimentTypeEnum;
import com.example.companyReputationManagement.models.enums.converters.SentimentTypeRefConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "review_insights")
public class ReviewInsight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "sentiment_type", nullable = false)
    @Convert(converter = SentimentTypeRefConverter.class)
    private SentimentTypeEnum sentimentType;

    @Column(name = "result_json", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private BotResponseDTO resultJson;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    public ReviewInsight(Long companyId, SentimentTypeEnum sentimentType, BotResponseDTO resultJson) {
        this.companyId = companyId;
        this.sentimentType = sentimentType;
        this.resultJson = resultJson;
    }
}
