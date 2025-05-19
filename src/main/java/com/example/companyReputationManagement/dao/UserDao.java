package com.example.companyReputationManagement.dao;

import com.example.companyReputationManagement.dto.user.find.FindByNameResponseListDTO;
import com.example.companyReputationManagement.models.CompanyUser;
import com.example.companyReputationManagement.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class UserDao {
    private final UserRepo userRepo;
    public CompanyUser save(CompanyUser companyUser){
        return userRepo.save(companyUser);
    }
    public CompanyUser findUserByUserName(String username){
        return userRepo.findUserByUsername(username);
    }

    public List<CompanyUser> findUsersByUserName(String username){
        return userRepo.findCompanyUsersByUsername(username);
    }

    public CompanyUser findUserByLoginOrEmail(String UserLogin, String UserEmail) {
        return userRepo.findFirstByUsernameOrEmail(UserLogin, UserEmail);
    }
    public CompanyUser findUserByLoginOrEmail(String userLoginOrEmail) {
        return userRepo.findFirstByUsernameOrEmail(userLoginOrEmail,userLoginOrEmail);
    }
    public Long findIdByUsernameOrEmail(String usernameOrEmail) {
        return userRepo.findCoreEntityIdByUsernameOrEmail(usernameOrEmail,usernameOrEmail);
    }
    public CompanyUser findUserByUserCode(String userCode) {
        return userRepo.findCompanyUserByUserCode(userCode);
    }
    public Long findUserIdByUserCode(String userCode) {
        return userRepo.findCompanyUserIdByUserCode(userCode);
    }

}
