package com.jangsacartel.biz.global.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {

	private final RedisTemplate<String, String> redisTemplate;

	// Refresh Token 유효 기간: 7일
	private final long refreshExpiration = 1000L * 60 * 60 * 24 * 7;

	public void saveRefreshToken(String providerId, String refreshToken) {
		redisTemplate.opsForValue().set("refresh:" + providerId, refreshToken, Duration.ofMillis(refreshExpiration));
	}

	public String getRefreshToken(String providerId) {
		return redisTemplate.opsForValue().get("refresh:" + providerId);
	}

	public void deleteRefreshToken(String providerId) {
		redisTemplate.delete("refresh:" + providerId);
	}
}