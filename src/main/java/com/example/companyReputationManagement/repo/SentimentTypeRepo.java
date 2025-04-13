package com.example.companyReputationManagement.repo;

import com.example.companyReputationManagement.models.RoleRefEntity;
import com.example.companyReputationManagement.models.SentimentTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SentimentTypeRepo extends JpaRepository<SentimentTypeEntity, Long> {
}
