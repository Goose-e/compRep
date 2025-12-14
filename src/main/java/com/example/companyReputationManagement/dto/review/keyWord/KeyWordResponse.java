package com.example.companyReputationManagement.dto.review.keyWord;

import com.example.companyReputationManagement.dto.review.keyWord.bot.BotResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import lombok.Getter;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

@Getter
public class KeyWordResponse extends HttpResponseBody<KeyWordResponseDTO> {
    private String httpRequestId = EMPTY_STRING;

    public KeyWordResponse() {
        super(EMPTY_STRING);
    }
}
