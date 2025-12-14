package com.example.companyReputationManagement.repo;

import com.example.companyReputationManagement.models.ReviewInsight;
import com.example.companyReputationManagement.models.enums.SentimentTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewInsightRepo extends JpaRepository<ReviewInsight, Long> {
    Optional<ReviewInsight> findTopByCompanyIdAndSentimentTypeOrderByCreatedAtDesc(Long companyId, SentimentTypeEnum sentimentType);
}
