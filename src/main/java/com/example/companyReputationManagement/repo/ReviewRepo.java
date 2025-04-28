package com.example.companyReputationManagement.repo;

import com.example.companyReputationManagement.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepo extends JpaRepository<Review, Long> {

    boolean existsByContentAndCompanyId(String content,Long companyId);
    List<Review> findAllByCompanyId(Long companyId);

}
