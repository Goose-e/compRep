package com.example.companyReputationManagement.iservice.services;

import com.example.companyReputationManagement.dto.lab.RequestTestDTO;
import com.example.companyReputationManagement.dto.lab.ResponseTest;
import com.example.companyReputationManagement.dto.lab.ResponseTestDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.ILabService;
import org.springframework.stereotype.Service;

import static com.example.companyReputationManagement.constants.SysConst.OC_BUGS;
import static com.example.companyReputationManagement.constants.SysConst.OC_OK;

@Service
public class LabService implements ILabService {
    @Override
    public HttpResponseBody<ResponseTestDTO> getVolume(RequestTestDTO requestTestDTO) {
        float volume = 30 * requestTestDTO.weight() + requestTestDTO.time() * 500 / 60;
        HttpResponseBody<ResponseTestDTO> response = new ResponseTest();

        response.setResponseEntity(new ResponseTestDTO(volume));
        response.setMessage("Всё хорошо");
        if (response.getErrors().isEmpty()) {
            response.setResponseCode(OC_OK);
        } else {
            response.setResponseCode(OC_BUGS);
        }
        return response;
    }
}
