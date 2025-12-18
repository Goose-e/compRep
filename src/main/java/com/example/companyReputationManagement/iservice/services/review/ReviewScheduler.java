package com.example.companyReputationManagement.iservice.services.review;

import com.example.companyReputationManagement.dao.CompanyDao;
import com.example.companyReputationManagement.dto.review.keyWord.KeyWordRequestDTO;
import com.example.companyReputationManagement.models.Company;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor

public class ReviewScheduler {
    private final CompanyDao companyDao;
    private final ReviewService reviewService;
    private final Logger log = LoggerFactory.getLogger(ReviewScheduler.class);
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Scheduled(cron = "0 0 3 * * *")
    public void scheduledUpdate() {
        updateAllCompaniesTrends();
    }

    public void updateAllCompaniesTrends() {
        List<Company> companies = companyDao.findAll();
        List<CompletableFuture<Void>> futures = companies.stream()
                .map(company -> CompletableFuture.runAsync(() -> {
                    try {
                        reviewService.parseReviews(company);
                        {
                            for (long i = 1; i < 5; i++) {
                                log.info("Running insights for company={}, sentiment={}", company.getName(), i);

                                KeyWordRequestDTO keyWordRequestDTO = new KeyWordRequestDTO(company.getCompanyCode(), i);
                                reviewService.keyWordAnalysis(keyWordRequestDTO);
                            }
                        }

                    } catch (Exception e) {
                        System.err.println("Error parsing trends for company: " + company.getName() + ", error: " + e.getMessage());
                    }
                }, executorService))
                .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }


}

