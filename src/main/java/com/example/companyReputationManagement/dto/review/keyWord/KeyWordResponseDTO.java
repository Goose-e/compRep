package com.example.companyReputationManagement.dto.review.keyWord;

import com.example.companyReputationManagement.dto.review.keyWord.bot.BotResponseDTO;
import com.example.companyReputationManagement.httpResponse.ResponseDto;

import java.sql.Timestamp;

public record KeyWordResponseDTO(
        BotResponseDTO botResponseDTO
) implements ResponseDto {
}
