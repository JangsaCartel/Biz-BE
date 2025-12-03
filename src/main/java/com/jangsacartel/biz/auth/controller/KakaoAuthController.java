package com.jangsacartel.biz.auth.controller;

import com.jangsacartel.biz.auth.dto.AdditionalUserInfo;
import com.jangsacartel.biz.auth.dto.KakaoTokenResponse;
import com.jangsacartel.biz.auth.dto.KakaoUserInfo;
import com.jangsacartel.biz.auth.service.KakaoAuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class KakaoAuthController {

	private final KakaoAuthService authService;

	// 1. 카카오 콜백 (로그인 시도)
	@GetMapping("/auth/login/kakao")
	public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code) {
		log.info("🚀 [Controller] 카카오 로그인 요청 받음");

		// 1. 토큰 및 유저정보 획득
		KakaoTokenResponse kakaoToken = authService.getKakaoAccessToken(code);
		KakaoUserInfo kakaoUser = authService.getUserInfo(kakaoToken.getAccessToken());

		// 2. 가입 여부 확인
		boolean exists = authService.existsUserByProviderId("kakao", kakaoUser.getProviderId());

		if (exists) {
			// [CASE A] 이미 가입됨 -> 바로 로그인 처리 (토큰 발급)
			log.info("✅ [Controller] 기존 회원 로그인 성공");
			String jwt = authService.loginOrRegisterUser(kakaoUser, null);

			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", jwt);

			// 응답 Body에 토큰을 같이 줘도 됩니다 (프론트 편의성)
			return ResponseEntity.ok()
				.headers(headers)
				.body(new TokenResponseWrapper(jwt, "LOGIN_SUCCESS"));
		} else {
			// [CASE B] 미가입 -> 회원가입 필요 메시지 + Provider ID 반환
			log.info("⚠️ [Controller] 신규 회원 -> 회원가입 필요");
			return ResponseEntity.status(HttpStatus.OK) // 200 OK로 보내되 상태값으로 구분
				.body(new SignupNeededResponse("NEED_SIGNUP", kakaoUser.getProviderId()));
		}
	}

	// 2. 최종 회원가입 (추가 정보 입력)
	@PostMapping("/auth/signup")
	public ResponseEntity<?> kakaoRegister(@RequestBody AdditionalUserInfo request,
		@RequestParam("providerId") String providerId) {
		log.info("📝 [Controller] 회원가입 요청 받음 (ProviderId: {})", providerId);

		KakaoUserInfo kakaoUser = new KakaoUserInfo();
		kakaoUser.setProvider("kakao");
		kakaoUser.setProviderId(providerId);

		// DB 저장 및 토큰 발급
		String jwt = authService.loginOrRegisterUser(kakaoUser, request);

		return ResponseEntity.ok()
			.header("Authorization", jwt)
			.body(new TokenResponseWrapper(jwt, "LOGIN_SUCCESS"));
	}

	// 응답용 내부 클래스들 (DTO로 빼도 됨)
	@lombok.Data
	@lombok.AllArgsConstructor
	static class TokenResponseWrapper {
		private String accessToken;
		private String status;
	}

	@lombok.Data
	@lombok.AllArgsConstructor
	static class SignupNeededResponse {
		private String status;
		private String providerId; // 프론트가 이걸 가지고 있다가 가입 때 보내줘야 함
	}
}