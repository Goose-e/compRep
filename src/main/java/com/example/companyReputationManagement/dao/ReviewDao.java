package com.example.companyReputationManagement.dao;

import com.example.companyReputationManagement.models.Review;
import com.example.companyReputationManagement.repo.ReviewRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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

    public void save(Review review) {
        reviewRepo.save(review);
    }

    public boolean existsByIdAndText(Long id, String text) {
        return reviewRepo.existsByContentAndCompanyId(text, id);
    }


}
