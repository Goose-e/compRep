package com.example.companyReputationManagement.models.enums.converters;


import com.example.companyReputationManagement.models.enums.RoleEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;


@Converter(autoApply = true)
public class RoleEnumConverter implements AttributeConverter<RoleEnum, Long> {

    @Override
    public Long convertToDatabaseColumn(RoleEnum roleEnum) {
        return roleEnum != null ? roleEnum.getId() : null;
    }

    @Override
    public RoleEnum convertToEntityAttribute(Long dbData) {
        return dbData != null ? RoleEnum.fromId(Math.toIntExact(dbData)) : null;
    }
}
