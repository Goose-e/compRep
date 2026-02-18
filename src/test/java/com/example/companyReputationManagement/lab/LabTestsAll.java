package com.example.companyReputationManagement.lab;

import com.example.companyReputationManagement.dto.lab.RequestTestDTO;
import com.example.companyReputationManagement.dto.lab.ResponseTestDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.services.LabService;
import com.example.companyReputationManagement.mapper.LabMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class LabTestsAll {
    @Mock
    private LabMapper labMapper;

    @InjectMocks
    private LabService labService;

    private RequestTestDTO request;

    @BeforeEach
    void setUp() {
        request = new RequestTestDTO(250.0f, 20.0f);
    }

    // --- SUCCESS CASE ---
    @Test
    void shouldReturnSuccessResponse() {
        ResponseTestDTO dto = new ResponseTestDTO(175.0f);

        when(labMapper.createResponse(250.0f, 20.0f)).thenReturn(dto);

        HttpResponseBody<ResponseTestDTO> response = labService.getVolume(request);

        assertEquals("OC_OK", response.getResponseCode());
        assertEquals("Всё хорошо", response.getMessage());
        assertNull(response.getError());
        assertNotNull(response.getResponseEntity());
        assertEquals(175.0f, response.getResponseEntity().volume());
        assertTrue(response.getErrors().isEmpty());
    }

    // --- NULL VOLUME (error case) ---
    @Test
    void shouldReturnErrorWhenVolumeIsNull() {
        ResponseTestDTO dto = new ResponseTestDTO(null);

        when(labMapper.createResponse(250.0f, 20.0f)).thenReturn(dto);

        HttpResponseBody<ResponseTestDTO> response = labService.getVolume(request);

        assertEquals("OC_BUGS", response.getResponseCode());

        assertEquals("Ошибка при подсчёте данных", response.getMessage());
        assertEquals("Ошибка подсчёта", response.getError());
        assertNull(response.getResponseEntity());
    }

    // --- VERIFY MAPPER CALLED ---
    @Test
    void shouldCallMapperOnce() {
        ResponseTestDTO dto = new ResponseTestDTO(175.0f);

        when(labMapper.createResponse(anyFloat(), anyFloat())).thenReturn(dto);

        labService.getVolume(request);

        verify(labMapper, times(1)).createResponse(250.0f, 20.0f);
    }
}

