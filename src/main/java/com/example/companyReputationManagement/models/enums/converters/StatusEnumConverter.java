package com.example.companyReputationManagement.models.enums.converters;


import com.example.companyReputationManagement.models.enums.StatusEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;


@Converter(autoApply = true) // autoApply = true означает, что конвертер будет применяться ко всем сущностям
public class StatusEnumConverter implements AttributeConverter<StatusEnum, Long> {

    @Override
    public Long convertToDatabaseColumn(StatusEnum statusEnum) {

        return statusEnum != null ? statusEnum.getId() : null;
    }

    @Override
    public StatusEnum convertToEntityAttribute(Long dbData) {

        return dbData != null ? StatusEnum.fromId(Math.toIntExact(dbData)) : null;
    }
}

