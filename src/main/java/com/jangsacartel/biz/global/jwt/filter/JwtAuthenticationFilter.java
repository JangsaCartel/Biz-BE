package com.jangsacartel.biz.global.jwt.filter;

import com.jangsacartel.biz.global.jwt.util.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		// 로그인 관련 API는 필터를 타지 않도록 설정 (이중 안전장치)
		String uri = request.getRequestURI();
	    String ctx = request.getContextPath();  // 보통 "/biz-be"
	    String path = (ctx != null && !ctx.isEmpty()) ? uri.substring(ctx.length()) : uri;
		return path.startsWith("/api/auth/")
				|| path.startsWith("/api/kakao/")
				|| path.startsWith("/v2/api-docs")
		        || path.startsWith("/swagger")
		        || path.startsWith("/swagger-resources")
		        || path.startsWith("/webjars");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);

			try {
				// 1. 토큰 검증
				Claims claims = jwtUtil.validateToken(token);

				String subject = claims.getSubject(); // provider:providerId
				String role = claims.get("role", String.class);

				// 2. provider, providerId 파싱
				String[] parts = subject.split(":", 2);
				String provider = parts.length > 1 ? parts[0] : "unknown";
				String providerId = parts.length > 1 ? parts[1] : subject;

				// 3. 인증 객체 생성
				CustomUserDetails userDetails = new CustomUserDetails(provider, providerId, role);

				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
					userDetails, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))
				);

				auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				// 4. SecurityContext에 등록
				SecurityContextHolder.getContext().setAuthentication(auth);

			} catch (ExpiredJwtException e) {
				log.error("❌ [Filter] 토큰 만료: {}", e.getMessage());
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType("application/json;charset=UTF-8");
				response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"토큰이 만료되었습니다.\"}");
				return; // 필터 중단
			} catch (Exception e) {
				log.error("❌ [Filter] 토큰 검증 오류: {}", e.getMessage(), e);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType("application/json;charset=UTF-8");
				response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"유효하지 않은 토큰입니다.\"}");
				return; // 필터 중단
			}
		} else {
			System.out.println("⚪ [Filter] Authorization 헤더 없음 또는 Bearer 아님 (익명 요청)");
		}

		filterChain.doFilter(request, response);
	}
}