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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtUtil jwtUtil;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.cors().and()
			.formLogin().disable()
			.httpBasic().disable()
			.authorizeHttpRequests(auth -> auth  // authorizeRequests -> authorizeHttpRequests 변경
				.antMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
				.anyRequest().authenticated()
			)
			.addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}