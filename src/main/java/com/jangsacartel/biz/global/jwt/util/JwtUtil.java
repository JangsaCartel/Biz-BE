package com.jangsacartel.biz.global.jwt.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secret;

	private Key key; // Key 객체를 저장할 필드

	// 토큰 유효 시간 설정
	private final long ACCESS_EXPIRE = 1000 * 60 * 60;            // 1시간
	private final long REFRESH_EXPIRE = 1000L * 60 * 60 * 24 * 7; // 7일
	private final long REGISTER_EXPIRE = 1000L * 60 * 10;         // 10분

	// [중요] @PostConstruct: 객체 생성 후 secret 값을 이용해 Key 객체를 미리 만들어둠
	@PostConstruct
	public void init() {
		// 비밀키가 너무 짧으면 에러가 날 수 있습니다. (32글자 이상 권장)
		// 문자열을 바이트로 변환하여 HMAC-SHA 키 생성
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	// 1. 로그인용 Access Token 발급
	public String generateAccessToken(String provider, String providerId, String role) {
		return Jwts.builder()
			.setSubject(provider + ":" + providerId)
			.claim("role", role)
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRE))
			.signWith(key, SignatureAlgorithm.HS256) // [수정됨] key 객체 사용
			.compact();
	}

	// 2. 재발급용 Refresh Token 발급
	public String generateRefreshToken(String provider, String providerId) {
		return Jwts.builder()
			.setSubject(provider + ":" + providerId)
			.setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRE))
			.signWith(key, SignatureAlgorithm.HS256) // [수정됨] key 객체 사용
			.compact();
	}

	// 3. 가입용 임시 토큰 발급
	public String generateRegisterToken(String provider, String providerId) {
		return Jwts.builder()
			.setSubject("REGISTER")
			.claim("provider", provider)
			.claim("providerId", providerId)
			.setExpiration(new Date(System.currentTimeMillis() + REGISTER_EXPIRE))
			.signWith(key, SignatureAlgorithm.HS256) // [수정됨] key 객체 사용
			.compact();
	}

	// 4. 가입용 토큰 해석
	public Map<String, String> parseRegisterToken(String token) {
		// [수정됨] setSigningKey에 key 객체 전달
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

		if (!"REGISTER".equals(claims.getSubject())) {
			throw new JwtException("올바른 가입 토큰이 아닙니다.");
		}

		Map<String, String> map = new HashMap<>();
		map.put("provider", (String) claims.get("provider"));
		map.put("providerId", (String) claims.get("providerId"));
		return map;
	}

	// 5. 일반 토큰 검증
	public Claims validateToken(String token) {
		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7);
		}
		// [수정됨] setSigningKey에 key 객체 전달
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}
}