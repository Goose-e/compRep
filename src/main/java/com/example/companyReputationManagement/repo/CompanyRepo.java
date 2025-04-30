package com.example.companyReputationManagement.repo;

import com.example.companyReputationManagement.dto.company.get.GetAllCompaniesResponseDTO;
import com.example.companyReputationManagement.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepo extends JpaRepository<Company, Long> {
    boolean existsByName(String name);

    Company findByName(String name);

    Company findByCompanyCode(String companyCode);

    @Query("SELECT new com.example.companyReputationManagement.dto.company.get.GetAllCompaniesResponseDTO(c.name,c.companyCode,c.industry,c.website,csu.sourceUrl)  FROM Company c JOIN CompanySourceUrl csu ON csu.companyId = c.coreEntityId WHERE c.status = 0")
    List<GetAllCompaniesResponseDTO> findAllActiveCompanyWithUrls();

    @Query("SELECT c FROM Company c  WHERE c.status = 0")
    List<Company> findAllActiveCompany();
}
