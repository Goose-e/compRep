package com.example.companyReputationManagement.dto.review.find;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import lombok.Data;

@Data
public class ReviewResponseDto implements ResponseDto {
    private String content;
    private int rating;
    private String author;
    private String sentiment;

    public ReviewResponseDto(String author, String content, int rating, String sentiment) {
        this.content = content;
        this.rating = rating;
        this.author = author;
        this.sentiment = sentiment;
    }
}
