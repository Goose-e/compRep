package com.example.companyReputationManagement.models;

import com.example.companyReputationManagement.models.enums.StatusEnum;
import com.example.companyReputationManagement.models.enums.converters.SentimentTypeRefConverter;
import com.example.companyReputationManagement.models.enums.converters.StatusEnumConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;


@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "core_entities")
public class CoreEntity {
    @Id
    @Column(name = "core_entity_id")
    private Long coreEntityId;
    @Column(name = "status_id")
    @Convert(converter = StatusEnumConverter.class)
    private StatusEnum status;
    @Column(name = "create_date")
    private LocalDateTime createDate;
    @Column(name = "delete_date", nullable = true)
    private LocalDateTime deleteDate;
    @PrePersist
    public void prePersist() {
        if (createDate == null) {
            createDate = LocalDateTime.now();
        }
        if (status == null) {
            status = StatusEnum.ACTUAL;
        }
    }public CoreEntity() {
        Random random = new Random();
        this.coreEntityId = random.nextLong(9000) + 1000;  // Диапазон от 1000 до 9999
    }



}
