package com.example.companyReputationManagement.iservice.services.lab;

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
        Float weight = requestTestDTO.weight();
        Float time = requestTestDTO.time();

        if (weight == null || time == null || weight < 5f || weight > 250f || time < 0f) {
            response.setError("Ошибка подсчёта");
            response.setMessage("Ошибка при подсчёте данных");
            response.setResponseEntity(null);

            if (response.getErrors().isEmpty()) response.setResponseCode(OC_OK);
            else response.setResponseCode(OC_BUGS);

            return response;
        }
        ResponseTestDTO responseTestDTO = labMapper.createResponse(weight, time);
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
