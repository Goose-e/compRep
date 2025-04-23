package com.example.companyReputationManagement.repo;

import com.example.companyReputationManagement.models.RoleRefEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRefRepo extends JpaRepository<RoleRefEntity, Long> {
}
