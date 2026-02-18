package com.example.companyReputationManagement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                        .content("{\"weight\":250, \"time\":20}"))
                .andExpect(status().isAccepted());
    }
}
