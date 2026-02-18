package com.example.companyReputationManagement.lab;

import com.example.companyReputationManagement.dto.lab.ResponseTestDTO;
import com.example.companyReputationManagement.iservice.services.lab.WeatherService;
import com.example.companyReputationManagement.mapper.LabMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LabTestsSec {

    @Mock
    private WeatherService weatherService;

    private LabMapper labMapper;

    @BeforeEach
    void setUp() {
        labMapper = new LabMapper(weatherService);
    }

    @Test
    void test1() {

        when(weatherService.getTemperature()).thenReturn(25f);

        float weight = 70f;
        float time = 31f;

        ResponseTestDTO res = labMapper.createResponse(weight, time);

        assertNotNull(res);
        assertEquals(2358.3f, res.volume(), 0.0001f);

        verify(weatherService, times(1)).getTemperature();
        verifyNoMoreInteractions(weatherService);
    }

    @Test
    void test2() {

        when(weatherService.getTemperature()).thenReturn(40f);

        float weight = 70f;
        float time = 31f;

        ResponseTestDTO res = labMapper.createResponse(weight, time);


        assertNotNull(res);
        assertEquals(3758.3f, res.volume(), 0.0001f);

        verify(weatherService, times(1)).getTemperature();
        verifyNoMoreInteractions(weatherService);
    }

    @Test
    void test3() {

        when(weatherService.getTemperature()).thenReturn(30f);

        float weight = 60f;
        float time = 0f;

        ResponseTestDTO res = labMapper.createResponse(weight, time);


        assertEquals(1800.0f, res.volume(), 0.0001f);

        verify(weatherService, times(1)).getTemperature();
        verifyNoMoreInteractions(weatherService);
    }

    @Test
    void test4() {

        when(weatherService.getTemperature()).thenReturn(31f);

        float weight = 10f;
        float time = 0f;

        ResponseTestDTO res = labMapper.createResponse(weight, time);


        assertEquals(455.0f, res.volume(), 0.0001f);

        verify(weatherService, times(1)).getTemperature();
        verifyNoMoreInteractions(weatherService);
    }

    @Test
    void test5() {

        when(weatherService.getTemperature()).thenReturn(25f);

        float weight = 1f;
        float time = 1f;


        ResponseTestDTO res = labMapper.createResponse(weight, time);


        assertEquals(38.3f, res.volume(), 0.0001f);

        verify(weatherService, times(1)).getTemperature();
        verifyNoMoreInteractions(weatherService);
    }

}

