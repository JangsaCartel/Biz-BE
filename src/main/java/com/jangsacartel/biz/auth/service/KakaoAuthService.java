package com.jangsacartel.biz.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jangsacartel.biz.auth.dto.AdditionalUserInfo;
import com.jangsacartel.biz.auth.dto.KakaoTokenResponse;
import com.jangsacartel.biz.auth.dto.KakaoUserInfo;
import com.jangsacartel.biz.global.jwt.service.TokenService;
import com.jangsacartel.biz.global.jwt.util.JwtUtil;
import com.jangsacartel.biz.user.entity.UserInfoVO;
import com.jangsacartel.biz.user.entity.UserVO;
import com.jangsacartel.biz.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Service
@RequiredArgsConstructor
public class KakaoAuthService {

	private final JwtUtil jwtUtil;
	private final UserMapper userMapper;
	private final TokenService tokenService;

	@Value("${kakao.client-id}")
	private String clientId;

	@Value("${kakao.redirect-uri}")
	private String redirectUri;

	// 1. 인가 코드로 카카오 액세스 토큰 요청
	public KakaoTokenResponse getKakaoAccessToken(String code) {
		log.info("📢 [Service] 카카오 토큰 요청 시작. code={}", code);
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("redirect_uri", redirectUri);
		params.add("code", code);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

		try {
			ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(
				"https://kauth.kakao.com/oauth/token", request, KakaoTokenResponse.class);
			log.info("✅ [Service] 카카오 토큰 획득 성공");
			return response.getBody();
		} catch (Exception e) {
			log.error("❌ [Service] 카카오 토큰 요청 실패: {}", e.getMessage());
			throw new RuntimeException("카카오 토큰 발급 실패", e);
		}
	}

	// 2. 액세스 토큰으로 유저 정보(providerId) 조회
	public KakaoUserInfo getUserInfo(String kakaoAccessToken) {
		log.info("📢 [Service] 카카오 유저 정보 요청 시작");
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(kakaoAccessToken);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<Void> request = new HttpEntity<>(headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(
				"https://kapi.kakao.com/v2/user/me", HttpMethod.GET, request, String.class
			);

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode root = objectMapper.readTree(response.getBody());

			KakaoUserInfo userInfo = new KakaoUserInfo();
			userInfo.setProvider("kakao");
			userInfo.setProviderId(String.valueOf(root.get("id").asLong()));

			log.info("✅ [Service] 카카오 유저 정보 획득: ID={}", userInfo.getProviderId());
			return userInfo;

		} catch (Exception e) {
			log.error("❌ [Service] 카카오 유저 정보 파싱 실패", e);
			throw new RuntimeException("카카오 사용자 정보 파싱 실패", e);
		}
	}

	// 3. 회원 존재 여부 확인
	public boolean existsUserByProviderId(String provider, String providerId) {
		UserVO user = userMapper.findByProviderAndProviderId(provider, providerId);
		boolean exists = (user != null);
		log.info("🔍 [Service] 회원 존재 여부 확인: {} (ID={})", exists, providerId);
		return exists;
	}

	// 4. 로그인 또는 회원가입 처리 (JWT 발급)
	@Transactional
	public String loginOrRegisterUser(KakaoUserInfo userInfo, AdditionalUserInfo additional) {
		String providerId = userInfo.getProviderId();
		UserVO user = userMapper.findByProviderAndProviderId("kakao", providerId);

		// 신규 회원이라면 DB 저장
		if (user == null && additional != null) {
			log.info("🆕 [Service] 신규 회원가입 진행: {}", additional.getNickname());

			// 1. User 테이블 저장
			UserVO newUser = UserVO.builder()
				.provider("kakao")
				.providerId(providerId)
				.nickname(additional.getNickname())
				.build();
			userMapper.insertUser(newUser);

			// 2. User_Info 테이블 저장
			UserInfoVO newInfo = UserInfoVO.builder()
				.userId(newUser.getUserId())
				.region(additional.getRegion())
				.userStoreName(additional.getUserStoreName())
				.businessType(additional.getBusinessType())
				.businessRegNo(additional.getBusinessRegNo())
				.businessStartDate(additional.getBusinessStartDate())
				.build();
			userMapper.insertUserInfo(newInfo);

			user = newUser; // 저장된 유저 객체 사용
		}

		// 토큰 생성 및 Redis 저장
		String refreshToken = jwtUtil.generateRefreshToken("kakao", providerId, "USER");
		tokenService.saveRefreshToken(providerId, refreshToken);

		log.info("🔑 [Service] JWT 토큰 발급 완료 (User ID: {})", user.getUserId());
		return jwtUtil.generateAccessToken("kakao", providerId, user.getUserId(), "USER");
	}
}