package com.example.companyReputationManagement.iservice;

import com.example.companyReputationManagement.dto.lab.RequestTestDTO;
import com.example.companyReputationManagement.dto.lab.ResponseTestDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

public interface ILabService {
    HttpResponseBody<ResponseTestDTO> getVolume(RequestTestDTO requestTestDTO);
}
