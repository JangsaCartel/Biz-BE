package com.jangsacartel.biz.user.controller;

import com.jangsacartel.biz.auth.dto.UserResponseDTO;
import com.jangsacartel.biz.auth.service.KakaoAuthService;
import com.jangsacartel.biz.global.jwt.filter.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final KakaoAuthService kakaoAuthService;

	// 내 정보 조회
	// 요청 주소: GET /api/users
	@GetMapping
	public ResponseEntity<UserResponseDTO> getUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {

		// [추가된 안전장치] 토큰 없이 들어왔을 경우 방어
		if (userDetails == null) {
			log.warn("❌ [Controller] 인증되지 않은 사용자의 접근입니다.");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 에러 반환
		}

		log.info("ℹ️ [Controller] 내 정보 조회 요청: {}", userDetails.getUsername());

		String providerId = userDetails.getProviderId();
		UserResponseDTO userInfo = kakaoAuthService.getUserInfoByProviderId(providerId);

		return ResponseEntity.ok(userInfo);
	}

}