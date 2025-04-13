package com.example.companyReputationManagement.models.enums.converters;

import com.example.companyReputationManagement.models.enums.SentimentTypeEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter(autoApply = true)
public class SentimentTypeRefConverter implements AttributeConverter<SentimentTypeEnum, Long> {

    @Override
    public Long convertToDatabaseColumn(SentimentTypeEnum sentimentTypeRef) {

        return sentimentTypeRef != null ? sentimentTypeRef.getId() : null;
    }

    @Override
    public SentimentTypeEnum convertToEntityAttribute(Long dbData) {
        // Преобразуем код из базы данных обратно в SentimentTypeRef
        return dbData != null ? SentimentTypeEnum.fromId(Math.toIntExact(dbData)) : null;
    }
}

