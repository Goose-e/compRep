package com.example.companyReputationManagement.mapper;

import com.example.companyReputationManagement.iservice.generate.GenerateCode;
import com.example.companyReputationManagement.models.SourceRef;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@AllArgsConstructor
@Component
public class SourceMapper {
    private final GenerateCode generateCode;


}
