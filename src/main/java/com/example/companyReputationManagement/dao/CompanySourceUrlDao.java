package com.example.companyReputationManagement.dao;


import com.example.companyReputationManagement.models.CompanySourceUrl;
import com.example.companyReputationManagement.models.enums.SourcesEnum;
import com.example.companyReputationManagement.repo.CompanySourceUrlRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CompanySourceUrlDao {
    private final CompanySourceUrlRepo companySourceUrlRepo;

    public void save(CompanySourceUrl companySourceUrl) {
        companySourceUrlRepo.save(companySourceUrl);
    }
    public CompanySourceUrl findByCompanyId(Long compId) {
       return companySourceUrlRepo.findByCompanyIdAndSourceType(compId, SourcesEnum.OTZOVIK);
    }
}
