package com.example.companyReputationManagement.iservice.services.lab;

import com.example.companyReputationManagement.dto.lab.RequestTestDTO;
import com.example.companyReputationManagement.dto.lab.ResponseTest;
import com.example.companyReputationManagement.dto.lab.ResponseTestDTO;
import com.example.companyReputationManagement.dto.lab.city.ResponseCity;
import com.example.companyReputationManagement.dto.lab.city.ResponseCityDTO;
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
        if (weight == null || time == null) {
            response.setError("Ошибка подсчёта");
            response.setMessage("нет значения веса либо времени");
            response.setResponseEntity(null);

            if (response.getErrors().isEmpty()) response.setResponseCode(OC_OK);
            else response.setResponseCode(OC_BUGS);

            return response;
        }
        if (weight < 5f || weight > 250f || time < 0f) {
            response.setError("Ошибка подсчёта");
            StringBuilder message = new StringBuilder();

            message.append(weight < 5f ? "Вес слишком маленький" : "вес слишком большой");

            message.append(time < 0f ? "Время не может быть отрицательнымм" : "");
            response.setMessage(message.toString());

            response.setResponseEntity(null);
            if (response.getErrors().isEmpty()) response.setResponseCode(OC_OK);
            else response.setResponseCode(OC_BUGS);

            return response;
        }
        Float temp = requestTestDTO.temp();
        ResponseTestDTO responseTestDTO = (temp == null)
                ? labMapper.createResponse(weight, time)
                : labMapper.createResponse(weight, time, temp);
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

    @Override
    public HttpResponseBody<ResponseCityDTO> getTemp(String city) {
        HttpResponseBody<ResponseCityDTO> response = new ResponseCity();
        if (city == null || city.isEmpty()) {
            response.setError("Город не найден");
            response.setMessage("Названия города пустое");
        } else {
            ResponseCityDTO responseCityDTO = labMapper.getTemp(city);
            response.setResponseEntity(responseCityDTO);
            response.setMessage("Температура в городе: " + city);
        }
        if (response.getErrors().isEmpty()) {
            response.setResponseCode(OC_OK);
        } else {
            response.setResponseCode(OC_BUGS);
        }
        return response;
    }
}
