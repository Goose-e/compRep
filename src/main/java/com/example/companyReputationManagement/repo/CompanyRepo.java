package com.example.companyReputationManagement.repo;

import com.example.companyReputationManagement.dto.company.find_by_name.FindCompanyByNameResponseDTO;
import com.example.companyReputationManagement.dto.company.get_all.GetAllCompaniesResponseDTO;
import com.example.companyReputationManagement.dto.company.get_all_company_users.GetAllCompanyUsersResponseDTO;
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

    @Query("SELECT new com.example.companyReputationManagement.dto.company.find_by_name.FindCompanyByNameResponseDTO(c.name, c.companyCode, c.industry, c.website, csu.sourceUrl, c.status) FROM Company c JOIN CompanySourceUrl csu ON csu.companyId = c.coreEntityId   WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :namePart, '%'))")
    List<FindCompanyByNameResponseDTO> findCompanyByNameContainingIgnoreCase(@Param("namePart") String name);

    Company findByCompanyCode(String companyCode);

    @Query("SELECT new com.example.companyReputationManagement.dto.company.get_all.GetAllCompaniesResponseDTO(c.name,c.companyCode,c.industry,c.website,csu.sourceUrl,c.status) " +
            " FROM Company c JOIN CompanySourceUrl csu ON csu.companyId = c.coreEntityId WHERE c.status = 0")
    List<GetAllCompaniesResponseDTO> findAllActiveCompanyWithUrls();

    @Query("SELECT c FROM Company c  WHERE c.status = 0")
    List<Company> findAllActiveCompany();

    @Query("SELECT new com.example.companyReputationManagement.dto.company.get_all_user_companies.GetAllUserCompaniesResponseDTO(c.name,c.companyCode,c.industry,c.website,csu.sourceUrl) " +
            "FROM Company c JOIN CompanySourceUrl csu " +
            "ON csu.companyId = c.coreEntityId JOIN UserCompanyRoles ucr ON ucr.companyId = c.coreEntityId JOIN CompanyUser cu ON cu.coreEntityId = ucr.userId " +
            "WHERE  cu.userCode =:userCode AND cu.status = 0 AND csu.status = 0")
    List<GetAllUserCompaniesResponseDTO> findAllActiveUserCompanies(@Param("userCode") String userCode);

    @Query("SELECT new com.example.companyReputationManagement.dto.company.get_by_code.GetCompanyByCodeResponseDTO(" +
            "c.name, c.companyCode, c.industry, c.website, csu.sourceUrl, " +
            "(SELECT ucr.role FROM UserCompanyRoles ucr JOIN CompanyUser cu ON cu.coreEntityId = ucr.userId " +
            "WHERE ucr.companyId = c.coreEntityId AND cu.userCode = :userCode),c.status) " +
            "FROM Company c " +
            "JOIN CompanySourceUrl csu ON csu.companyId = c.coreEntityId " +
            "WHERE c.status = 0 AND c.companyCode = :companyCode")
    GetCompanyByCodeResponseDTO findActiveCompanyByCodeWithUrls(@Param("companyCode") String companyCode, @Param("userCode") String userCode);

    @Query("SELECT new com.example.companyReputationManagement.dto.company.get_all_company_users.GetAllCompanyUsersResponseDTO(cu.username,cu.userCode,ucr.role,ucr.status) FROM Company c " +
            "LEFT JOIN UserCompanyRoles ucr ON c.coreEntityId = ucr.companyId " +
            "JOIN CompanyUser cu ON cu.coreEntityId = ucr.userId WHERE c.companyCode =:companyCode")
    List<GetAllCompanyUsersResponseDTO> findAllCompanyUsers(@Param("companyCode") String companyCode);

}
