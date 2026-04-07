package com.example.companyReputationManagement.controllers.labController;

import com.example.companyReputationManagement.dto.lab.city.ResponseCityDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.ILabService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/weather")
public class WeatherController {
    private final ILabService labService;

    @GetMapping("/{city}")
    public HttpResponseBody<ResponseCityDTO> getVolume(@Valid @PathVariable String city) {
        return labService.getTemp(city);
    }
}
