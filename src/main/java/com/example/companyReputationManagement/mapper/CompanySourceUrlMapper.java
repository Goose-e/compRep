package com.example.companyReputationManagement.mapper;

import com.example.companyReputationManagement.iservice.generate.GenerateCode;
import com.example.companyReputationManagement.models.CompanySourceUrl;
import com.example.companyReputationManagement.models.enums.SourcesEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class CompanySourceUrlMapper {
    private final GenerateCode generateCode;

    public CompanySourceUrl create(Long compId, String url) {
        CompanySourceUrl companySourceUrl = new CompanySourceUrl();
        companySourceUrl.setCompanyId(compId);
        companySourceUrl.setCompanySourceUrlCode(generateCode.generateCode(companySourceUrl));
        companySourceUrl.setSourceUrl(url == null ? "не найдено" : url);
        companySourceUrl.setSourceType(SourcesEnum.OTZOVIK);
        return companySourceUrl;
    }
    public CompanySourceUrl edit(String url, CompanySourceUrl companySourceUrl) {
        companySourceUrl.setSourceUrl(url == null ? "не найдено" : url);
        return companySourceUrl;
    }
}
