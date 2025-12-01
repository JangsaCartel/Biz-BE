package com.jangsacartel.biz.config;

import com.jangsacartel.biz.global.jwt.filter.JwtAuthenticationFilter;
import com.jangsacartel.biz.global.jwt.util.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtUtil jwtUtil;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			// cors 설정을 security에 적용
			.cors().configurationSource(corsConfigurationSource())
			.and()
			.formLogin().disable()
			.httpBasic().disable()
			.authorizeHttpRequests(auth -> auth
				.antMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
				.anyRequest().authenticated()
			)
			.addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	// CORS 허용 설정 빈 등록
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();

		// 프론트엔드 주소 허용 (포트 5173)
		config.setAllowedOrigins(List.of("http://localhost:5173"));

		// 허용할 HTTP 메서드
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

		// 허용할 헤더
		config.setAllowedHeaders(List.of("*"));

		// 헤더에 담긴 토큰을 프론트에서 읽을 수 있게 허용 (Register-Token 등)
		config.setExposedHeaders(List.of("Authorization", "Register-Token"));

		// 쿠키나 인증 정보를 포함한 요청 허용
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}