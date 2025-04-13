package com.example.companyReputationManagement.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "sentiment_type_ref")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SentimentTypeEntity {
    @Id
    Long SentimentTypeId;
    String SentimentTypeCode;
    String SentimentName;
}
