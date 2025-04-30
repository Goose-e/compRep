package com.example.companyReputationManagement.iservice.services.transactions;

import com.example.companyReputationManagement.dao.CompanyDao;
import com.example.companyReputationManagement.dao.CompanySourceUrlDao;
import com.example.companyReputationManagement.dao.UserCompanyRolesDao;
import com.example.companyReputationManagement.dao.UserDao;
import com.example.companyReputationManagement.models.Company;
import com.example.companyReputationManagement.models.CompanySourceUrl;
import com.example.companyReputationManagement.models.UserCompanyRoles;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CompanyTrans {
    private final CompanyDao companyDao;
    private final UserCompanyRolesDao userCompanyRolesdao;
    private final CompanySourceUrlDao companySourceUrlDao;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Transactional
    public void save(Company company, UserCompanyRoles userCompanyRoles, CompanySourceUrl companySourceUrl) {
        try{
            companyDao.save(company);
            userCompanyRolesdao.save(userCompanyRoles);
            companySourceUrlDao.save(companySourceUrl);
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during save operation", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during save operation", e);
            throw new RuntimeException("Unexpected error", e);
        }
    }
    @Transactional
    public void saveCompanyAndRole(Company company, UserCompanyRoles userCompanyRoles) {
        try{
            companyDao.save(company);
            userCompanyRolesdao.save(userCompanyRoles);
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during save operation", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during save operation", e);
            throw new RuntimeException("Unexpected error", e);
        }
    }

    @Transactional
    public void updateCompany(Company company,CompanySourceUrl companySourceUrl) {
        try {
            companyDao.save(company);
            companySourceUrlDao.save(companySourceUrl);
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during save operation", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during save operation", e);
            throw new RuntimeException("Unexpected error", e);
        }
    }

    @Transactional
    public void saveCompanyUserRole(UserCompanyRoles userCompanyRoles) {
        try {
            userCompanyRolesdao.save(userCompanyRoles);
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during save operation", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during save operation", e);
            throw new RuntimeException("Unexpected error", e);
        }
    }

    @Transactional
    public void changeUserRoleOwner(UserCompanyRoles userCompanyRolesOwner,UserCompanyRoles userCompanyRolesNewOwner) {
        try {
            userCompanyRolesdao.save(userCompanyRolesOwner);
            userCompanyRolesdao.save(userCompanyRolesNewOwner);
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during save operation", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during save operation", e);
            throw new RuntimeException("Unexpected error", e);
        }
    }


}
