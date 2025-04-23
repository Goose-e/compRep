package com.example.companyReputationManagement.models.enums.converters;

import com.example.companyReputationManagement.models.enums.SourcesEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SourceEnumConverter implements AttributeConverter<SourcesEnum, Long> {

    @Override
    public Long convertToDatabaseColumn(SourcesEnum sourcesEnum) {
        return sourcesEnum != null ? sourcesEnum.getSourceCode() : null;
    }

    @Override
    public SourcesEnum convertToEntityAttribute(Long dbData) {
        return dbData != null ? SourcesEnum.fromId(dbData) : null;
    }
}

