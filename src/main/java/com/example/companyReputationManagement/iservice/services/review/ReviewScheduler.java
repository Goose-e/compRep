package com.example.companyReputationManagement.iservice.services.review;

import com.example.companyReputationManagement.dao.CompanyDao;
import com.example.companyReputationManagement.dao.ReviewDao;
import com.example.companyReputationManagement.models.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
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

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Scheduled(cron = "0 0 3 * * *")
    @Order(1)
    public void updateAllCompaniesTrends() {
        List<Company> companies = companyDao.findAll();
        List<CompletableFuture<Void>> futures = companies.stream()
                .map(company -> CompletableFuture.runAsync(() -> {
                    try {
                        reviewService.parseReviews(company);
                    } catch (Exception e) {
                        System.err.println("Error parsing trends for company: " + company.getName() + ", error: " + e.getMessage());
                    }
                }, executorService))
                .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }


}

