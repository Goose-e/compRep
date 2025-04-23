package com.example.companyReputationManagement.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


@Setter
@Getter
@Entity
@Table(name = "source_ref")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SourceRef {
    @Id
    private Long sourceId;
    private String name;
    private String url;


}
