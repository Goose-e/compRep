package com.example.companyReputationManagement.dto.review.get_all;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import lombok.Getter;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

@Getter
public class GetReviewResponse extends HttpResponseBody<GetReviewResponseListDto> {
    private String httpRequestId = EMPTY_STRING;

    public GetReviewResponse() {
        super(EMPTY_STRING);
    }

}
