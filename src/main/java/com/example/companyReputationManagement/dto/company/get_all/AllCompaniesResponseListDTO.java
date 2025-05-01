package com.example.companyReputationManagement.dto.company.get_all;

import com.example.companyReputationManagement.dto.company.get_all_user_companies.GetAllUserCompaniesResponseDTO;
import com.example.companyReputationManagement.httpResponse.ResponseDto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class AllCompaniesResponseListDTO implements ResponseDto {

    private List<GetAllCompaniesResponseDTO> allCompanies;

    public AllCompaniesResponseListDTO(List<GetAllCompaniesResponseDTO> allCompanies) {
        this.allCompanies = allCompanies;
    }
}
