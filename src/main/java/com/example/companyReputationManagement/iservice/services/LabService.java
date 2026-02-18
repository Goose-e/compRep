package com.example.companyReputationManagement.iservice.services;

import com.example.companyReputationManagement.dto.lab.RequestTestDTO;
import com.example.companyReputationManagement.dto.lab.ResponseTest;
import com.example.companyReputationManagement.dto.lab.ResponseTestDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.ILabService;
import com.example.companyReputationManagement.mapper.LabMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.companyReputationManagement.constants.SysConst.OC_BUGS;
import static com.example.companyReputationManagement.constants.SysConst.OC_OK;

@Service
@RequiredArgsConstructor
public class LabService implements ILabService {
    private final LabMapper labMapper;

    @Override
    public HttpResponseBody<ResponseTestDTO> getVolume(RequestTestDTO requestTestDTO) {
        HttpResponseBody<ResponseTestDTO> response = new ResponseTest();
        ResponseTestDTO responseTestDTO = labMapper.createResponse(requestTestDTO.weight(), requestTestDTO.time());
        if (responseTestDTO.volume() == null) {
            response.setError("Ошибка подсчёта");
            response.setMessage("Ошибка при подсчёте данных");
        } else {
            response.setResponseEntity(responseTestDTO);
            response.setMessage("Всё хорошо");
        }
        if (response.getErrors().isEmpty()) {
            response.setResponseCode(OC_OK);
        } else {
            response.setResponseCode(OC_BUGS);
        }
        return response;
    }
}
