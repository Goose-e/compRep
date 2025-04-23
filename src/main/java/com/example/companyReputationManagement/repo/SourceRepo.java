package com.example.companyReputationManagement.repo;

import com.example.companyReputationManagement.models.SourceRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceRepo extends JpaRepository<SourceRef, Long> {
}
