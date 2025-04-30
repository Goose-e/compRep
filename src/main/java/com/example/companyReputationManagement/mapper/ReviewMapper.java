package com.example.companyReputationManagement.mapper;


import com.example.companyReputationManagement.dto.review.find.ReviewResponseDto;
import com.example.companyReputationManagement.dto.review.get_all.GetReviewResponseDto;
import com.example.companyReputationManagement.dto.review.get_all_by_sent.GetAllBySentResponseDTO;
import com.example.companyReputationManagement.dto.review.get_all_by_sent.GetAllBySentResponseListDTO;
import com.example.companyReputationManagement.iservice.generate.GenerateCode;
import com.example.companyReputationManagement.models.Review;
import com.example.companyReputationManagement.models.enums.SentimentTypeEnum;
import com.example.companyReputationManagement.models.enums.SourcesEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@Component
public class ReviewMapper {
    private final GenerateCode generateCode;


    public Review createReview(String content, String author, int rating, Long companyId, Long id, Timestamp date) {
        Review review = new Review();
        review.setContent(content);
        review.setReviewerName(author);
        review.setReviewCode(generateCode.generateCode(review));
        review.setCompanyId(companyId);
        review.setSourceId(SourcesEnum.fromId(id));
        review.setRating(rating);
        review.setPublishedDate(date);
        switch (rating) {
            case 0:
                review.setSentimentTypeId(SentimentTypeEnum.UNRATED);
                break;
            case 1:
            case 2:
                review.setSentimentTypeId(SentimentTypeEnum.NEGATIVE);
                break;
            case 3:
                review.setSentimentTypeId(SentimentTypeEnum.NEUTRAL);
                break;
            case 4:
            case 5:
                review.setSentimentTypeId(SentimentTypeEnum.POSITIVE);
                break;

            default:
                throw new IllegalArgumentException("Invalid rating: " + rating);
        }


        return review;
    }

    public ReviewResponseDto mapReviewToReviewResponseDto(Review review) {
        return new ReviewResponseDto(review.getReviewerName(), review.getContent(), review.getRating(),
                review.getSentimentTypeId() != null ? review.getSentimentTypeId().getType() : null, review.getPublishedDate());
    }

    public GetReviewResponseDto mapReviewToGetReviewResponseDto(Review review) {
        return new GetReviewResponseDto(review.getReviewerName(), review.getContent(), review.getRating(),
                review.getSentimentTypeId() != null ? review.getSentimentTypeId().getType() : null, review.getPublishedDate());
    }

    public GetAllBySentResponseListDTO mapListToGetAllBySentResponseListDTO(String compName, List<GetAllBySentResponseDTO> listReviews, String sentType) {
        return new GetAllBySentResponseListDTO(compName, sentType, listReviews);
    }
}
