package com.example.companyReputationManagement.repo;

import com.example.companyReputationManagement.models.StatusRefEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRefRepository extends JpaRepository<StatusRefEntity, Long> {
}
