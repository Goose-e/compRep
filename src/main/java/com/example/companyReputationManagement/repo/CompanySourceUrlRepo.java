package com.example.companyReputationManagement.repo;

import com.example.companyReputationManagement.models.Company;
import com.example.companyReputationManagement.models.CompanySourceUrl;
import com.example.companyReputationManagement.models.enums.SourcesEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanySourceUrlRepo extends JpaRepository<CompanySourceUrl, Long> {

    CompanySourceUrl findByCompanyIdAndSourceType(Long companyId, SourcesEnum sourceType);
}
