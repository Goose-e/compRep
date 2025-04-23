package com.example.companyReputationManagement.dao;

import com.example.companyReputationManagement.models.Review;
import com.example.companyReputationManagement.repo.CompanyRepo;
import com.example.companyReputationManagement.repo.ReviewRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class ReviewDao {
    private final ReviewRepo reviewRepo;
    public void saveAll(List<Review> reviews){
        reviewRepo.saveAll(reviews);
    }
}
