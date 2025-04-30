package com.example.companyReputationManagement.dto.review.get_all_by_sent;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import lombok.Getter;

import java.util.List;


public record GetAllBySentResponseListDTO(String companyName,String sentType,List <GetAllBySentResponseDTO> reviewsBySentType) implements ResponseDto {
}
