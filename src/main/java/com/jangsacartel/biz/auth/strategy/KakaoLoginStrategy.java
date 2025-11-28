package com.jangsacartel.biz.auth.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jangsacartel.biz.auth.dto.SocialUserInfo;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoLoginStrategy implements SocialLoginStrategy {

	// application.properties에 설정된 값들을 가져옵니다.
	@Value("${kakao.client-id}")
	private String clientId;

	@Value("${kakao.redirect-uri}")
	private String redirectUri;

	// 1. 토큰 받아오기
	@Override
	public String getAccessToken(String authCode) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("redirect_uri", redirectUri);
		params.add("code", authCode);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

		// 카카오에게 요청 전송
		ResponseEntity<JsonNode> response = restTemplate.postForEntity(
			"https://kauth.kakao.com/oauth/token", request, JsonNode.class);

		// 응답에서 access_token만 쏙 꺼냄
		return response.getBody().get("access_token").asText();
	}

	// 2. 유저 정보 받아오기
	@Override
	public SocialUserInfo getUserInfo(String accessToken) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken); // 헤더에 토큰 장착
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<Void> request = new HttpEntity<>(headers);

		// 카카오에게 정보 요청
		ResponseEntity<JsonNode> response = restTemplate.exchange(
			"https://kapi.kakao.com/v2/user/me", HttpMethod.GET, request, JsonNode.class
		);

		JsonNode body = response.getBody();
		String id = String.valueOf(body.get("id").asLong());

		// 닉네임 가져오기 (카카오 구조: properties -> nickname)
		String nickname = "";
		if (body.has("properties") && body.get("properties").has("nickname")) {
			nickname = body.get("properties").get("nickname").asText();
		}

		// 공통 명함(SocialUserInfo)에 담아서 리턴
		return SocialUserInfo.builder()
			.providerId(id)
			.nickname(nickname)
			.build();
	}

	// 3. 내 담당 이름
	@Override
	public String getProviderName() {
		return "kakao";
	}
}