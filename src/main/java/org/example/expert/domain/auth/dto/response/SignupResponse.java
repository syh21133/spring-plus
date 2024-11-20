package org.example.expert.domain.auth.dto.response;

import lombok.Getter;

@Getter
public class SignupResponse {

    private final String bearerToken;
    private final String fileUrl;

    public SignupResponse(String bearerToken, String fileUrl) {
        this.bearerToken = bearerToken;
        this.fileUrl = fileUrl;
    }
}
