package com.example.companyReputationManagement.lab;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LabTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testLab() throws Exception {
        mockMvc.perform(post("/tests/get_volume")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"weight\":5, \"time\":3}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("OC_OK"))
                .andExpect(jsonPath("$.message").value("Всё хорошо"))
                .andExpect(jsonPath("$.error").doesNotExist())
                .andExpect(jsonPath("$.responseEntity.volume").value(175.0))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isEmpty());
    }



}
