package com.jangsacartel.biz.global.jwt.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {

	private final RedisTemplate<String, String> redisTemplate;

	// 토큰 저장 (key: "RT:카카오ID", value: "토큰값", 유효시간: 7일)
	public void saveRefreshToken(String providerId, String refreshToken) {
		redisTemplate.opsForValue().set("RT:" + providerId, refreshToken, Duration.ofDays(7));
	}

	// 토큰 조회
	public String getRefreshToken(String providerId) {
		return redisTemplate.opsForValue().get("RT:" + providerId);
	}

	// 토큰 삭제 (로그아웃 시)
	public void deleteRefreshToken(String providerId) {
		redisTemplate.delete("RT:" + providerId);
	}
}