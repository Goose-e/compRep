package com.example.companyReputationManagement.models;

import com.example.companyReputationManagement.models.enums.StatusEnum;
import com.example.companyReputationManagement.models.enums.converters.StatusEnumConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Random;


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
    }

    public CoreEntity() {
        long timePart = System.currentTimeMillis() % 1_000_000_000L;
        int randomPart = new Random().nextInt(1000);
        this.coreEntityId = timePart * 1000 + randomPart;
    }


}
