package com.jangsacartel.biz.auth.controller;

import com.jangsacartel.biz.auth.service.AuthService;
import com.jangsacartel.biz.user.dto.UserRegisterRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	// 1. 로그인 (카카오, Biz 등)
	// 요청 예시: GET /api/auth/login/kakao?code=인가코드
	@GetMapping("/login/{provider}")
	public ResponseEntity<?> login(@PathVariable String provider, @RequestParam String code) {
		return ResponseEntity.ok(authService.socialLogin(provider, code));
	}

	// 2. 회원가입 (추가 정보 입력 후)
	// 요청 예시: POST /api/auth/signup (헤더에 Register-Token 포함)
	@PostMapping("/signup")
	public ResponseEntity<?> signup(
		@RequestHeader("Register-Token") String token,
		@RequestBody UserRegisterRequest request) {

		return ResponseEntity.ok(authService.register(token, request));
	}
}