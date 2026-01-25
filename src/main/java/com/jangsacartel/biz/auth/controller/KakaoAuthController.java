package com.jangsacartel.biz.auth.controller;

import com.jangsacartel.biz.auth.dto.AdditionalUserInfoDTO;
import com.jangsacartel.biz.auth.dto.KakaoTokenResponse;
import com.jangsacartel.biz.auth.dto.KakaoUserInfo;
import com.jangsacartel.biz.auth.service.KakaoAuthService;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Api(tags = "카카오 인증 컨트롤러")
public class KakaoAuthController {

	private final KakaoAuthService authService;

	// 1. 카카오 콜백 (로그인 시도)
	@ApiOperation(
	        value = "카카오 로그인 콜백",
	        notes =
	            "카카오 인가코드(code)를 받아 로그인/회원가입 여부를 판별합니다.\n" +
	            "- 기존 회원이면 JWT를 발급하여 응답합니다.\n" +
	            "- 신규 회원이면 회원가입이 필요하다는 상태와 providerId를 응답합니다.\n" +
	            "- (참고) 로그인 성공 시 Authorization 응답 헤더에도 JWT가 포함됩니다."
	    )
	    @ApiImplicitParams({
	        @ApiImplicitParam(
	            name = "code",
	            value = "카카오 인가코드",
	            required = true,
	            paramType = "query",
	            dataType = "string",
	            example = "4sZK7EeihwLiHE5FUSmCqQr..."
	        )
	    })
	    @ApiResponses({
	    	@ApiResponse(code = 200, message = "성공 (status로 LOGIN_SUCCESS / NEED_SIGNUP 구분)", response = Object.class),
	        @ApiResponse(code = 400, message = "잘못된 요청(파라미터 누락/형식 오류)"),
	        @ApiResponse(code = 500, message = "서버 내부 오류 또는 외부 카카오 API 처리 오류")
	    })
	@GetMapping("/auth/login/kakao")
	public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code) {

		// 1. 토큰 및 유저정보 획득
		KakaoTokenResponse kakaoToken = authService.getKakaoAccessToken(code);
		KakaoUserInfo kakaoUser = authService.getUserInfo(kakaoToken.getAccessToken());

		// 2. 가입 여부 확인
		boolean exists = authService.existsUserByProviderId("kakao", kakaoUser.getProviderId());

		if (exists) {
			// [CASE A] 이미 가입됨 -> 바로 로그인 처리 (토큰 발급)
			String jwt = authService.loginOrRegisterUser(kakaoUser, null);

			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", jwt);

			// 응답 Body에 토큰을 같이 줘도 됩니다 (프론트 편의성)
			return ResponseEntity.ok()
				.headers(headers)
				.body(new TokenResponseWrapper(jwt, "LOGIN_SUCCESS"));
		} else {
			// [CASE B] 미가입 -> 회원가입 필요 메시지 + Provider ID 반환
			return ResponseEntity.status(HttpStatus.OK) // 200 OK로 보내되 상태값으로 구분
				.body(new SignupNeededResponse("NEED_SIGNUP", kakaoUser.getProviderId()));
		}
	}

	// 2. 최종 회원가입 (추가 정보 입력)
	@ApiOperation(
	        value = "카카오 회원가입 완료",
	        notes =
	            "추가 정보를 입력받아 회원가입을 완료하고 JWT를 발급합니다.\n" +
	            "- providerId는 로그인 콜백에서 NEED_SIGNUP 응답으로 받은 값을 전달해야 합니다.\n" +
	            "- (참고) 가입 성공 시 Authorization 응답 헤더에도 JWT가 포함됩니다."
	    )
	    @ApiImplicitParams({
	        @ApiImplicitParam(
	            name = "providerId",
	            value = "카카오 providerId (로그인 콜백에서 받은 값)",
	            required = true,
	            paramType = "query",
	            dataType = "string",
	            example = "1234567890"
	        )
	    })
	    @ApiResponses({
	        @ApiResponse(code = 200, message = "회원가입 성공(JWT 발급)", response = TokenResponseWrapper.class),
	        @ApiResponse(code = 400, message = "요청 바디/파라미터 오류"),
	        @ApiResponse(code = 500, message = "서버 내부 오류")
	    })
	@PostMapping("/auth/signup")
	public ResponseEntity<?> kakaoRegister(
			@ApiParam(value = "회원가입 추가 정보", required = true)
			@RequestBody AdditionalUserInfoDTO request,
			@RequestParam("providerId") String providerId) 
	{

		KakaoUserInfo kakaoUser = new KakaoUserInfo();
		kakaoUser.setProvider("kakao");
		kakaoUser.setProviderId(providerId);

		// DB 저장 및 토큰 발급
		String jwt = authService.loginOrRegisterUser(kakaoUser, request);

		return ResponseEntity.ok()
			.header("Authorization", jwt)
			.body(new TokenResponseWrapper(jwt, "LOGIN_SUCCESS"));
	}

	// 응답용 내부 클래스들 (DTO로 빼도 됨)
	@lombok.Data
	@lombok.AllArgsConstructor
	@ApiModel(description = "로그인/회원가입 성공 시 토큰 응답")
	static class TokenResponseWrapper {
		@ApiModelProperty(value = "JWT AccessToken", example = "eyJhbGciO...")
		private String accessToken;
		
		@ApiModelProperty(value = "상태값", example = "LOGIN_SUCCESS")
		private String status;
	}

	@lombok.Data
	@lombok.AllArgsConstructor
	@ApiModel(description = "신규 회원 - 회원가입 필요 응답")
	static class SignupNeededResponse {
		@ApiModelProperty(value = "상태값", example = "NEED_SIGNUP")
		private String status;
		
		@ApiModelProperty(value = "회원가입 시 전달해야 하는 providerId", example = "1234567890")
		private String providerId; // 프론트가 이걸 가지고 있다가 가입 때 보내줘야 함
	}
}