package com.example.companyReputationManagement.controllers.labController;

import com.example.companyReputationManagement.dto.lab.RequestTestDTO;
import com.example.companyReputationManagement.dto.lab.ResponseTestDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.ILabService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/water")
public class FuncController {
    private final ILabService labService;

    @PostMapping("/norm")
    public ResponseEntity<HttpResponseBody<ResponseTestDTO>> getVolume(
            @Valid @RequestBody RequestTestDTO requestTestDTO) {

        HttpResponseBody<ResponseTestDTO> response = labService.getVolume(requestTestDTO);

        if (requestTestDTO.weight() == 2f) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }


}
