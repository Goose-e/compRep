package com.example.companyReputationManagement.models.enums;

import com.example.companyReputationManagement.models.RoleRefEntity;
import com.example.companyReputationManagement.models.SentimentTypeEntity;
import com.example.companyReputationManagement.models.SourceRef;
import com.example.companyReputationManagement.models.StatusRefEntity;
import com.example.companyReputationManagement.repo.RoleRefRepo;
import com.example.companyReputationManagement.repo.SentimentTypeRepo;
import com.example.companyReputationManagement.repo.SourceRepo;
import com.example.companyReputationManagement.repo.StatusRefRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnumInit {

    private final StatusRefRepository statusRefRepository;
    private final RoleRefRepo roleRefRepository;
    private final SentimentTypeRepo sentimentTypeRepo;
    private final SourceRepo sourceRepo;

    @PostConstruct
    public void initAllRefs() {
        initStatusRef();
        initRoleRef();
        initSentimentTypeRef();
        initSourceRef();
    }

    private void initStatusRef() {
        for (StatusEnum statusEnum : StatusEnum.values()) {
            if (!statusRefRepository.existsById((long) statusEnum.getId())) {
                StatusRefEntity entity = StatusRefEntity.builder()
                        .status_id((long) statusEnum.getId())
                        .status_code(statusEnum.getCode())
                        .status_name(statusEnum.getStatus())
                        .build();
                statusRefRepository.save(entity);
            }
        }
    }

    private void initRoleRef() {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (!roleRefRepository.existsById(roleEnum.getId())) {
                RoleRefEntity entity = RoleRefEntity.builder()
                        .role_ref_id(roleEnum.getId())
                        .role_code(roleEnum.getCode())
                        .role_name(roleEnum.getRole())
                        .build();
                roleRefRepository.save(entity);
            }
        }
    }

    private void initSourceRef() {
        for (SourcesEnum sourceEnum : SourcesEnum.values()) {
            if (!sourceRepo.existsById(sourceEnum.getSourceCode())) {
                SourceRef entity = SourceRef.builder()
                        .sourceId(sourceEnum.getSourceCode())
                        .name(sourceEnum.getName())
                        .url(sourceEnum.getUrl())
                        .build();
                sourceRepo.save(entity);
            }
        }
    }


    private void initSentimentTypeRef() {
        for (SentimentTypeEnum sentiment : SentimentTypeEnum.values()) {
            if (!sentimentTypeRepo.existsById((long) sentiment.getId())) {
                SentimentTypeEntity entity = SentimentTypeEntity.builder()
                        .SentimentTypeId((long) sentiment.getId())
                        .SentimentTypeCode(sentiment.getCode())
                        .SentimentName(sentiment.getType())
                        .build();
                sentimentTypeRepo.save(entity);
            }
        }
    }
}
