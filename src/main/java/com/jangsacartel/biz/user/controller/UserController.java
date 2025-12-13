package com.jangsacartel.biz.user.controller;

import com.jangsacartel.biz.auth.dto.UserResponseDTO;
import com.jangsacartel.biz.auth.service.KakaoAuthService;
import com.jangsacartel.biz.global.jwt.filter.CustomUserDetails;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import springfox.documentation.annotations.ApiIgnore;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor

@Api(tags = "유저 컨트롤러")
public class UserController {

	private final KakaoAuthService kakaoAuthService;

	// 내 정보 조회
	// 요청 주소: GET /api/users
	
	@ApiOperation(
            value = "내 정보 조회",
            notes =
                    "현재 로그인한 사용자의 정보를 조회합니다.\n" +
                    "- Authorization 헤더에 Bearer 토큰이 필요합니다.\n" +
                    "- 인증이 없으면 401(UNAUTHORIZED)을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상 호출", response = UserResponseDTO.class),
            @ApiResponse(code = 401, message = "인증 실패(토큰 없음/만료/위조)"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
	@GetMapping
	public ResponseEntity<UserResponseDTO> getUserInfo(
			@ApiIgnore
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		
		// [추가된 안전장치] 토큰 없이 들어왔을 경우 방어
		if (userDetails == null) {
			log.warn("❌ [Controller] 인증되지 않은 사용자의 접근입니다.");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 에러 반환
		}

		log.info("ℹ️ [Controller] 내 정보 조회 요청: {}", userDetails.getUsername());

		String providerId = userDetails.getProviderId();
		UserResponseDTO userInfo = kakaoAuthService.getUserInfoByProviderId(providerId);

		return ResponseEntity.ok(userInfo);
	}

}