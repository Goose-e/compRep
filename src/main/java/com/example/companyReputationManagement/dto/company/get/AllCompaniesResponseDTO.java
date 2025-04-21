package com.example.companyReputationManagement.dto.company.get;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class AllCompaniesResponseDTO implements ResponseDto {
    private List<GetAllCompaniesResponseDTO> allCompanies;

    public AllCompaniesResponseDTO(List<GetAllCompaniesResponseDTO> allCompanies) {
        this.allCompanies = allCompanies;
    }
}
