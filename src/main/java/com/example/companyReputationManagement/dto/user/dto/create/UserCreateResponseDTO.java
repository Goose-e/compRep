package com.example.companyReputationManagement.dto.user.dto.create;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import lombok.Data;

@Data
public class UserCreateResponseDTO implements ResponseDto {
    private String email;
    private String username;

    public UserCreateResponseDTO(String email, String username) {
        this.email = email;
        this.username = username;
    }
}
