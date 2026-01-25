package com.jangsacartel.biz.global.jwt.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Log4j2
@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secret;

	private Key key;

	private final long ACCESS_EXPIRE = 1000 * 60 * 60;            // 1시간
	private final long REFRESH_EXPIRE = 1000L * 60 * 60 * 24 * 7; // 7일

	@PostConstruct
	public void init() {
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	// 토큰 검증 (여기서 예외가 발생하면 Filter가 잡습니다)
	public Claims validateToken(String token) {
		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7).trim();
		}

		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	// Access Token 발급
	public String generateAccessToken(String provider, String providerId, Long userId, String role) {
		return Jwts.builder()
			.setSubject(provider + ":" + providerId)
			.claim("provider", provider)
			.claim("providerId", providerId)
			.claim("userId", userId)
			.claim("role", role)
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRE))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	// Refresh Token 발급
	public String generateRefreshToken(String provider, String providerId, String role) {
		return Jwts.builder()
			.setSubject(provider + ":" + providerId)
			.claim("role", role)
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRE))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}
}