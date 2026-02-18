package com.example.companyReputationManagement.lab;

import com.example.companyReputationManagement.dto.lab.RequestTestDTO;
import com.example.companyReputationManagement.iservice.services.LabService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LabTestsServiceFirst {

    @Autowired
    private LabService labService;

    // --- invalid kg ---
    @Test
    void testKgTooSmall() {
        var r1 = labService.getVolume(new RequestTestDTO(4.9f, 0f));
        assertNull(r1.getResponseEntity());
        assertEquals("Ошибка подсчёта", r1.getError());

        var r2 = labService.getVolume(new RequestTestDTO(0f, 0f));
        assertNull(r2.getResponseEntity());
        assertEquals("Ошибка подсчёта", r2.getError());
    }

    @Test
    void testKgTooLarge() {
        var r = labService.getVolume(new RequestTestDTO(251f, 0f));
        assertNull(r.getResponseEntity());
        assertEquals("Ошибка подсчёта", r.getError());
    }

    // --- invalid mins ---
    @Test
    void testMinsNegative() {
        var r = labService.getVolume(new RequestTestDTO(5f, -1f));
        assertNull(r.getResponseEntity());
        assertEquals("Ошибка подсчёта", r.getError());
    }

    // --- boundary kg valid ---
    @Test
    void testKgBoundaries() {
        var r1 = labService.getVolume(new RequestTestDTO(5f, 0f));
        assertNotNull(r1.getResponseEntity());
        assertEquals(150.0f, r1.getResponseEntity().volume(), 0.0001f);

        var r2 = labService.getVolume(new RequestTestDTO(250f, 0f));
        assertNotNull(r2.getResponseEntity());
        assertEquals(7500.0f, r2.getResponseEntity().volume(), 0.0001f);
    }

    // --- valid computations ---
    @Test
    void testValidExamples() {
        var r1 = labService.getVolume(new RequestTestDTO(7f, 0f));
        assertNotNull(r1.getResponseEntity());
        assertEquals(210.0f, r1.getResponseEntity().volume(), 0.0001f);

        var r2 = labService.getVolume(new RequestTestDTO(60f, 30f));
        assertNotNull(r2.getResponseEntity());
        assertEquals(2050.0f, r2.getResponseEntity().volume(), 0.0001f);
    }

    @Test
    void testLargeTime() {
        var r1 = labService.getVolume(new RequestTestDTO(250f, 600f));
        assertNotNull(r1.getResponseEntity());
        assertEquals(12500.0f, r1.getResponseEntity().volume(), 0.0001f);

        var r2 = labService.getVolume(new RequestTestDTO(250f, 1000f));
        assertNotNull(r2.getResponseEntity());
        assertEquals(15833.3f, r2.getResponseEntity().volume(), 0.01f);
    }
}
