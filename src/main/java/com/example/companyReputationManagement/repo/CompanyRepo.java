package com.example.companyReputationManagement.repo;

import com.example.companyReputationManagement.dto.company.get_all.GetAllCompaniesResponseDTO;
import com.example.companyReputationManagement.dto.company.get_all_user_companies.GetAllUserCompaniesResponseDTO;
import com.example.companyReputationManagement.dto.company.get_by_code.GetCompanyByCodeResponseDTO;
import com.example.companyReputationManagement.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepo extends JpaRepository<Company, Long> {
    boolean existsByName(String name);

    Company findByName(String name);

    Company findByCompanyCode(String companyCode);

    @Query("SELECT new com.example.companyReputationManagement.dto.company.get_all.GetAllCompaniesResponseDTO(c.name,c.companyCode,c.industry,c.website,csu.sourceUrl)  FROM Company c JOIN CompanySourceUrl csu ON csu.companyId = c.coreEntityId WHERE c.status = 0")
    List<GetAllCompaniesResponseDTO> findAllActiveCompanyWithUrls();

    @Query("SELECT c FROM Company c  WHERE c.status = 0")
    List<Company> findAllActiveCompany();

    @Query("SELECT new com.example.companyReputationManagement.dto.company.get_all_user_companies.GetAllUserCompaniesResponseDTO(c.name,c.companyCode,c.industry,c.website,csu.sourceUrl) FROM Company c JOIN CompanySourceUrl csu " +
            "ON csu.companyId = c.coreEntityId JOIN UserCompanyRoles ucr ON ucr.companyId = c.coreEntityId JOIN CompanyUser cu ON cu.coreEntityId = ucr.userId WHERE c.status = 0 AND cu.userCode =:userCode")
    List<GetAllUserCompaniesResponseDTO> findAllActiveUserCompanies(@Param("userCode") String userCode);

    @Query("SELECT new com.example.companyReputationManagement.dto.company.get_by_code.GetCompanyByCodeResponseDTO(c.name,c.companyCode,c.industry,c.website,csu.sourceUrl)  FROM Company c JOIN CompanySourceUrl csu " +
            "ON csu.companyId = c.coreEntityId WHERE c.status = 0 AND c.companyCode =:companyCode")
    GetCompanyByCodeResponseDTO findCompanyByCodeWithUrls(@Param("companyCode") String companyCode);
}
