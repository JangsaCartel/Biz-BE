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

import com.jangsacartel.biz.auth.dto.UserResponseDTO;

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

	/**
	 * ✅ 1. 내 정보 조회 (DB 숫자 -> 프론트 문자열 변환)
	 * DB에서 꺼낸 숫자(예: 12345)를 "0000012345"로 바꿔서 줍니다.
	 */
	public UserResponseDTO getUserInfo(Long userId) {
		// 1. DB에서 정보 조회 (이때 UserInfoVO 안에는 Long 타입 숫자가 들어있음)
		UserInfoVO userInfo = userMapper.findUserInfoByUserId(userId);
		UserVO user = userMapper.findUserById(userId); // 닉네임 등을 위해 필요하다면

		// 2. [핵심 로직] 포맷팅 (빈 자리를 0으로 채움)
		String formattedRegNo = null;
		if (userInfo.getBusinessRegNo() != null) {
			// %010d: 10자리 십진수, 빈 곳은 0으로 채움
			formattedRegNo = String.format("%010d", userInfo.getBusinessRegNo());
		}

		// 3. 응답 DTO 생성
		return UserResponseDTO.builder()
			.nickname(user.getNickname())
			.region(userInfo.getRegion())
			.userStoreName(userInfo.getUserStoreName())
			.businessType(userInfo.getBusinessType())
			.businessRegNo(formattedRegNo) // 변환된 문자열 주입
			.businessStartDate(userInfo.getBusinessStartDate())
			.build();
	}

	/**
	 * ✅ 2. 사업자 번호 중복 검사 (프론트 문자열 -> DB 숫자 비교)
	 * 프론트가 "0000012345"를 보내도 숫자로 바꾸면 12345가 되므로 DB와 비교 가능합니다.
	 */
	public boolean checkDuplicateRegNo(String inputRegNo) {
		try {
			// 문자열을 숫자로 변환 (앞의 0은 자연스럽게 사라짐)
			Integer regNoAsInt = Integer.parseInt(inputRegNo);

			// [사업자등록번호 int 타입 저장으로 생략된 0 확인 로그]
			System.out.println("👉 입력받은 값: " + inputRegNo);
			System.out.println("👉 변환된 숫자: " + regNoAsInt);

			// DB에 해당 숫자가 있는지 확인
			return userMapper.existsByBusinessRegNo(regNoAsInt);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("사업자 번호는 숫자 형식이어야 합니다.");
		}
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

	public UserResponseDTO getUserInfoByProviderId(String providerId) {
		// 1. 소셜 ID로 내부 회원 ID(PK) 찾기
		UserVO user = userMapper.findByProviderAndProviderId("kakao", providerId);

		if (user == null) {
			throw new RuntimeException("회원 정보를 찾을 수 없습니다.");
		}

		// 2. 내부 회원 ID로 기존에 만든 상세 조회 로직 재사용
		return getUserInfo(user.getUserId());
	}
}