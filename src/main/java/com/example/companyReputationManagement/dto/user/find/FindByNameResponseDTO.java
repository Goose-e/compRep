package com.example.companyReputationManagement.dto.user.find;

public record FindByNameResponseDTO(
        String username,
        String userCode,
        String email
) {
}
