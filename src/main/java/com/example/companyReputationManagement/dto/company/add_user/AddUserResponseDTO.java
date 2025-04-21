package com.example.companyReputationManagement.dto.company.add_user;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import lombok.Data;

import java.io.Serial;

@Data
public class AddUserResponseDTO implements ResponseDto {
    @Serial
    private static final long serialVersionUID = 1L;
    private String username;

    public AddUserResponseDTO(String username) {
        this.username = username;
    }
}
