package com.example.companyReputationManagement.iservice.services.transactions;

import com.example.companyReputationManagement.dao.ReviewDao;
import com.example.companyReputationManagement.models.Review;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewsTrans {
    private final ReviewDao reviewDao;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Transactional
    public void saveAll(List<Review> reviews) {
        try {
            reviews.forEach(
                    review -> {
                        if (!reviewDao.existsByIdAndText(review.getCompanyId(), review.getContent())) {
                            reviewDao.save(review);
                        }
                    }
            );
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during save operation", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during save operation", e);
            throw new RuntimeException("Unexpected error", e);
        }
    }
}
