package com.example.companyReputationManagement.repo;

import com.example.companyReputationManagement.dto.review.get_all_by_sent.GetAllBySentRequestDTO;
import com.example.companyReputationManagement.dto.review.get_all_by_sent.GetAllBySentResponseDTO;
import com.example.companyReputationManagement.models.Review;
import com.example.companyReputationManagement.models.enums.SentimentTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepo extends JpaRepository<Review, Long> {

    boolean existsByContentAndCompanyId(String content,Long companyId);
    List<Review> findAllByCompanyId(Long companyId);

    List<Review> findAllByCompanyIdOrderByPublishedDateAsc(Long companyId);

    @Query("SELECT new com.example.companyReputationManagement.dto.review.get_all_by_sent.GetAllBySentResponseDTO(r.reviewerName,r.content,r.rating,r.publishedDate) " +
            "FROM Review r WHERE r.companyId =:companyId AND r.sentimentTypeId =:sentimentTypeId")
    List<GetAllBySentResponseDTO> findAllByCompanyIdAndSentimentTypeId(@Param("companyId")Long companyId,@Param("sentimentTypeId") SentimentTypeEnum sentimentTypeId);
}
