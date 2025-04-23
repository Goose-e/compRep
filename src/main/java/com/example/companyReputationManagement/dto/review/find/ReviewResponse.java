package com.example.companyReputationManagement.dto.review.find;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import jakarta.servlet.http.HttpServlet;
import lombok.Getter;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

@Getter
public class ReviewResponse extends HttpResponseBody<ReviewResponseListDto> {
    private String httpRequestId = EMPTY_STRING;

    public ReviewResponse() {
        super(EMPTY_STRING);
    }

}
