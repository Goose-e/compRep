package com.example.companyReputationManagement.dto.review.find;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class ReviewResponseDto implements ResponseDto {
    private String content;
    private int rating;
    private String author;
    private String sentiment;
    private Timestamp timestamp;

    public ReviewResponseDto(String author, String content, int rating, String sentiment,Timestamp timestamp) {
        this.content = content;
        this.rating = rating;
        this.author = author;
        this.sentiment = sentiment;
        this.timestamp = timestamp;
    }
}
