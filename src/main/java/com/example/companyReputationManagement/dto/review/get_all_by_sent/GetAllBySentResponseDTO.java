package com.example.companyReputationManagement.dto.review.get_all_by_sent;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import lombok.Getter;

import java.sql.Timestamp;

public record GetAllBySentResponseDTO(String author,String content, int rating,
                                      Timestamp timestamp) implements ResponseDto {
}
