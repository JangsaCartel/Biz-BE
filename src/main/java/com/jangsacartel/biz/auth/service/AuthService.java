package com.jangsacartel.biz.auth.service;

import com.jangsacartel.biz.auth.dto.SocialUserInfo;
import com.jangsacartel.biz.auth.strategy.SocialLoginStrategy;
import com.jangsacartel.biz.global.jwt.service.TokenService;
import com.jangsacartel.biz.global.jwt.util.JwtUtil;
import com.jangsacartel.biz.user.dto.UserRegisterRequest;
import com.jangsacartel.biz.user.entity.UserInfoVO;
import com.jangsacartel.biz.user.entity.UserVO;
import com.jangsacartel.biz.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final List<SocialLoginStrategy> strategies; // '카카오 통역사' 자동 주입
	private final UserMapper userMapper;
	private final JwtUtil jwtUtil;
	private final TokenService tokenService;

	// 1. 소셜 로그인 시도
	public Map<String, Object> socialLogin(String provider, String authCode) {
		// (1) 통역사(strategy) 찾기
		SocialLoginStrategy strategy = strategies.stream()
			.filter(s -> s.getProviderName().equals(provider))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("지원하지 않는 로그인 방식입니다."));

		// (2) 소셜 정보 받아오기
		String token = strategy.getAccessToken(authCode);
		SocialUserInfo socialUser = strategy.getUserInfo(token);

		// (3) 우리 DB 조회
		UserVO user = userMapper.findByProviderAndProviderId(provider, socialUser.getProviderId());

		Map<String, Object> result = new HashMap<>();

		if (user != null) {
			// [CASE A] 이미 가입된 회원 -> 로그인 성공
			// UserVO에 role이 없으므로 "USER"로 고정 (또는 필요시 DB에서 조회)
			String accessToken = jwtUtil.generateAccessToken(provider, user.getProviderId(), "USER");
			String refreshToken = jwtUtil.generateRefreshToken(provider, user.getProviderId());

			tokenService.saveRefreshToken(user.getProviderId(), refreshToken);

			result.put("status", "LOGIN_SUCCESS");
			result.put("accessToken", accessToken);
			result.put("refreshToken", refreshToken);
		} else {
			// [CASE B] 신규 방문자 -> 회원가입 필요
			String registerToken = jwtUtil.generateRegisterToken(provider, socialUser.getProviderId());

			result.put("status", "NEED_SIGNUP");
			result.put("registerToken", registerToken);
			result.put("tempNickname", socialUser.getNickname());
		}

		return result;
	}

	// 2. 최종 회원가입 (DB 저장)
	@Transactional
	public Map<String, String> register(String registerToken, UserRegisterRequest request) {
		// (1) 가입용 토큰 검증
		Map<String, String> info = jwtUtil.parseRegisterToken(registerToken);
		String provider = info.get("provider");
		String providerId = info.get("providerId");

		// (2) [User 테이블] 저장
		UserVO user = UserVO.builder()
			.nickname(request.getNickname()) // dto -> request 수정됨
			.provider(provider)
			.providerId(providerId)
			.build();
		userMapper.insertUser(user); // 여기서 user.userId가 생성됨

		// (3) [User_Info 테이블] 저장
		UserInfoVO userInfo = UserInfoVO.builder()
			.userId(user.getUserId()) // FK 연결
			.region(request.getRegion())
			.userStoreName(request.getUserStoreName())
			.businessType(request.getBusinessType())
			.businessRegNo(request.getBusinessRegNo())
			.businessStartDate(request.getBusinessStartDate())
			.build();
		userMapper.insertUserInfo(userInfo);

		// (4) 정식 토큰 발급
		String accessToken = jwtUtil.generateAccessToken(provider, providerId, "USER");
		String refreshToken = jwtUtil.generateRefreshToken(provider, providerId);

		tokenService.saveRefreshToken(providerId, refreshToken);

		Map<String, String> tokens = new HashMap<>();
		tokens.put("accessToken", accessToken);
		tokens.put("refreshToken", refreshToken);
		return tokens;
	}
}