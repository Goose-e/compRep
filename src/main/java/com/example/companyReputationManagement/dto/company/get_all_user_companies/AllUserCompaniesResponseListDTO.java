package com.example.companyReputationManagement.dto.company.get_all_user_companies;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class AllUserCompaniesResponseListDTO implements ResponseDto {

    private List<GetAllUserCompaniesResponseDTO> allCompanies;

    public AllUserCompaniesResponseListDTO(List<GetAllUserCompaniesResponseDTO> allCompanies) {
        this.allCompanies = allCompanies;
    }
}
