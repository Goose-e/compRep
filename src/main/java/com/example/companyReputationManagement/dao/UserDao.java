package com.example.companyReputationManagement.dao;

import com.example.companyReputationManagement.models.CompanyUser;
import com.example.companyReputationManagement.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public CompanyUser findUserByLoginOrEmail(String UserLogin, String UserEmail) {
        return userRepo.findUserByUsernameOrEmail(UserLogin, UserEmail);
    }
    public CompanyUser findUserByLoginOrEmail(String userLoginOrEmail) {
        return userRepo.findUserByUsernameOrEmail(userLoginOrEmail,userLoginOrEmail);
    }

}
