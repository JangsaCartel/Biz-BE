package com.jangsacartel.biz.global.jwt.filter;

import com.jangsacartel.biz.global.jwt.util.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// 1. 헤더에서 토큰 추출
		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7); // "Bearer " 제거

			try {
				// 2. 토큰 유효성 검사 및 Claims 추출
				Claims claims = jwtUtil.validateToken(token);
				String providerId = claims.getSubject(); // "kakao:12345" 형태
				String role = claims.get("role", String.class); // "USER"

				if (providerId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

					// 3. 인증 객체 생성 (권한 부여)
					// "ROLE_" 접두사는 스프링 시큐리티 표준 규격
					SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
					List<SimpleGrantedAuthority> authorities = Collections.singletonList(authority);

					// 인증된 객체 만들기 (비밀번호는 없으므로 null)
					UsernamePasswordAuthenticationToken authenticationToken =
						new UsernamePasswordAuthenticationToken(providerId, null, authorities);

					authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					// 4. 시큐리티 컨텍스트에 등록 (통과!)
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				}
			} catch (Exception e) {
				log.error("토큰 검증 실패: {}", e.getMessage());
				// 에러가 나도 일단 다음 필터로 넘겨서 시큐리티가 막게 함
			}
		}

		filterChain.doFilter(request, response);
	}
}