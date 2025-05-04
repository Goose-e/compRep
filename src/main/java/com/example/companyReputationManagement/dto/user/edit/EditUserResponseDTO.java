package com.example.companyReputationManagement.dto.user.edit;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import lombok.Data;

@Data
public class EditUserResponseDTO implements ResponseDto {
    String newUsername;
    String newEmail;

    public EditUserResponseDTO(String newUsername, String newEmail) {
        this.newUsername = newUsername;
        this.newEmail = newEmail;
    }

}
