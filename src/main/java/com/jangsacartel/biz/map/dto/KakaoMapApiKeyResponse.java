package com.jangsacartel.biz.map.dto;

import lombok.Data;

@Data
public class KakaoMapApiKeyResponse {
    private String kakaoMapApiKey;

    public KakaoMapApiKeyResponse(String kakaoMapApiKey) {
        this.kakaoMapApiKey = kakaoMapApiKey;
    }
}
