package com.example.companyReputationManagement.dao;

import com.example.companyReputationManagement.models.SourceRef;
import com.example.companyReputationManagement.repo.SourceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@RequiredArgsConstructor
@Component
public class SourceDao {
    private final SourceRepo sourceRepo;
    public void saveAll(Collection<SourceRef> sources) {
        sourceRepo.saveAll(sources);
    }
}
