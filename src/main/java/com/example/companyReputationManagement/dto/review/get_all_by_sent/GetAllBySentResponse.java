package com.example.companyReputationManagement.dto.review.get_all_by_sent;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import lombok.Getter;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

@Getter
public class GetAllBySentResponse extends HttpResponseBody<GetAllBySentResponseListDTO> {
    private String httpRequestId = EMPTY_STRING;

    public GetAllBySentResponse() {
        super(EMPTY_STRING);
    }
}
