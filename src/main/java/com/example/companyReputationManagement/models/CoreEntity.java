package com.example.companyReputationManagement.models;

import com.example.companyReputationManagement.models.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "core_entities")
public class CoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "core_entity_id")
    private Long coreEntityId;

    @Column(name = "status_id")
    private StatusEnum status;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "delete_date", nullable = true)
    private LocalDateTime deleteDate;


    public CoreEntity() {
    }


    public CoreEntity(StatusEnum status, LocalDateTime createDate, LocalDateTime deleteDate) {
        this.status = status;
        this.createDate = createDate;
        this.deleteDate = deleteDate;
    }


}
