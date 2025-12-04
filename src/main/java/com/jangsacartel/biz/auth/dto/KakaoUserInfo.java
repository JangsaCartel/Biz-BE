package com.jangsacartel.biz.auth.dto;

import lombok.Data;

@Data
public class KakaoUserInfo {

	// 소셜로그인 제공자(ex.kakao)
	private String provider;

	// 소셜로그인 고유 ID (회원 식별자)
	private String providerId;

}