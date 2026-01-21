package com.jangsacartel.biz.config;

import com.jangsacartel.biz.global.jwt.filter.JwtAuthenticationFilter;
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

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.cors().configurationSource(corsConfigurationSource()) // CORS 설정 적용
			.and()
			.formLogin().disable()
			.httpBasic().disable()
			.authorizeHttpRequests(auth -> auth
				    .requestMatchers("/api/auth/**", "/api/kakao/**", "/api/map/**").permitAll()
				    .requestMatchers(
				            "/swagger-ui.html",
				            "/v2/api-docs",
				            "/swagger-resources/**",
				            "/swagger-resources",
				            "/webjars/**"
				        ).permitAll()
				    // ✅ springfox(swagger2) 허용
				    .requestMatchers(
				    		"/biz-be/swagger-ui.html",
				    	    "/biz-be/v2/api-docs",
				    	    "/biz-be/swagger-resources/**",
				    	    "/biz-be/swagger-resources",
				    	    "/biz-be/webjars/**"
				    ).permitAll()

				 // ✅ swagger-ui가 로딩 중 찌르는 부가 요청들
				    .requestMatchers("/", "/csrf").permitAll()

				    // (선택) 컨텍스트 포함 케이스 대비
				    .requestMatchers("/biz-be/", "/biz-be/csrf").permitAll()
				    
				    .anyRequest().authenticated()
				)
			// JWT 필터 추가
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:5173")); // 프론트 주소
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);
		// 클라이언트가 헤더의 토큰을 읽을 수 있게 허용
		config.setExposedHeaders(List.of("Authorization", "Register-Token"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}