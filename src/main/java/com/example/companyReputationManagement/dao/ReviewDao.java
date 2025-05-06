package com.example.companyReputationManagement.dao;

import com.example.companyReputationManagement.dto.review.get_all_by_sent.GetAllBySentResponseDTO;
import com.example.companyReputationManagement.models.Review;
import com.example.companyReputationManagement.models.enums.SentimentTypeEnum;
import com.example.companyReputationManagement.repo.ReviewRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ReviewDao {
    private final ReviewRepo reviewRepo;

    public void saveAll(List<Review> reviews) {
        reviewRepo.saveAll(reviews);
    }

    public List<Review> findAllByCompanyId(Long companyId) {
        return reviewRepo.findAllByCompanyId(companyId);
    }

    public List<Review> findAllByCompanyIdSorted(Long companyId) {
        return reviewRepo.findAllByCompanyIdOrderByPublishedDateAsc(companyId);
    }
    public List<Review> findAllByCompanyIdForMonth(Long companyId,Timestamp startTimestamp,Timestamp endTimestamp) {
        return reviewRepo.findAllByCompanyIdForMonth(companyId, startTimestamp, endTimestamp);
    }

    public void save(Review review) {
        reviewRepo.save(review);
    }

    public boolean existsByIdAndText(Long id, String text) {
        return reviewRepo.existsByContentAndCompanyId(text, id);
    }

    public List<GetAllBySentResponseDTO> findAllBySentType(Long compId, SentimentTypeEnum sentId) {
        return reviewRepo.findAllByCompanyIdAndSentimentTypeId(compId, sentId);
    }

    public Double getAverageRating(Long compId, Timestamp start, Timestamp end) {
        return reviewRepo.getAverageRating(compId,start,end);
    }
}
