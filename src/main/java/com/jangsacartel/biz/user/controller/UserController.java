package com.jangsacartel.biz.user.controller;

import java.util.List;

import com.jangsacartel.biz.auth.dto.UserResponseDTO;
import com.jangsacartel.biz.auth.service.KakaoAuthService;
import com.jangsacartel.biz.global.jwt.filter.CustomUserDetails;
import com.jangsacartel.biz.user.dto.CommentUpdateRequestDTO;
import com.jangsacartel.biz.user.dto.MyCommentDTO;
import com.jangsacartel.biz.user.dto.MyPageProfileResponseDTO;
import com.jangsacartel.biz.user.dto.MyPostDTO;
import com.jangsacartel.biz.user.dto.NicknameUpdateRequestDTO;
import com.jangsacartel.biz.user.dto.RegionUpdateRequestDTO;
import com.jangsacartel.biz.user.dto.StoreNameUpdateRequestDTO;
import com.jangsacartel.biz.user.dto.UserProfileDTO;
import com.jangsacartel.biz.user.service.UserService;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Api(tags = "유저 컨트롤러")
public class UserController {

	private final KakaoAuthService kakaoAuthService;
	private final UserService userService;

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
	public ResponseEntity<UserProfileDTO> getUserInfo(
			@ApiIgnore
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		// [안전장치] 토큰 없이 들어왔을 경우 방어
		if (userDetails == null) {
			log.warn("❌ [Controller] 인증되지 않은 사용자의 접근입니다.");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 에러 반환
		}

		log.info("ℹ️ [Controller] 내 정보 조회 요청: {}", userDetails.getUsername());

		String providerId = userDetails.getProviderId();

		// 1. 서비스에서 전체 정보 가져오기 (여기엔 토큰이 포함된 자식 객체가 있을 수 있음)
		UserResponseDTO fullInfo = kakaoAuthService.getUserInfoByProviderId(providerId);

		// 2. [보안 핵심] 안전한 DTO로 데이터 옮겨 담기 (매핑)
		// 토큰 필드가 아예 없는 UserProfileDTO를 사용하여 원천 차단
		UserProfileDTO safeProfile = UserProfileDTO.builder()
			.nickname(fullInfo.getNickname())
			.region(fullInfo.getRegion())
			.userStoreName(fullInfo.getUserStoreName())
			.businessType(fullInfo.getBusinessType())
			.businessRegNo(fullInfo.getBusinessRegNo())
			.businessStartDate(fullInfo.getBusinessStartDate())
			.build();

		return ResponseEntity.ok(safeProfile);
	}

	// 마이페이지 닉네임/상호명/지역 조회 - UserService 사용
	@ApiOperation(value = "마이페이지 프로필 조회", notes = "마이페이지용 정보(닉네임, 상호명, 지역)를 조회합니다.")
	@GetMapping("/mypage")
	public ResponseEntity<MyPageProfileResponseDTO> getMyPageInfo(
		@ApiIgnore @AuthenticationPrincipal CustomUserDetails userDetails) {

		if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		// UserService를 통해 PK(userId) 획득
		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());

		return ResponseEntity.ok(userService.getMyPageProfile(userId));
	}

	// 마이페이지 닉네임 수정 - UserService 사용
	@ApiOperation(value = "닉네임 수정", notes = "유저의 닉네임을 수정합니다.")
	@PatchMapping("/nickname")
	public ResponseEntity<String> updateNickname(
		@ApiIgnore @AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody NicknameUpdateRequestDTO requestDTO) {

		if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		// UserService를 통해 PK(userId) 획득
		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());

		userService.updateNickname(userId, requestDTO.getNickname());

		return ResponseEntity.ok("닉네임이 수정되었습니다.");
	}

	// 마이페이지 내가 쓴 게시글 조회 - UserService 사용
	@ApiOperation(value = "내가 쓴 게시글 조회", notes = "작성한 게시글 목록을 반환합니다.")
	@GetMapping("/posts")
	public ResponseEntity<List<MyPostDTO>> getMyPosts(
		@ApiIgnore @AuthenticationPrincipal CustomUserDetails userDetails) {

		if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		// UserService를 통해 PK(userId) 획득
		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());

		return ResponseEntity.ok(userService.getMyPosts(userId));
	}

	// 마이페이지 내 게시글 삭제 - UserService 사용
	@ApiOperation(value = "내 게시글 삭제", notes = "게시글 ID를 받아 삭제(Soft Delete) 처리합니다.")
	@DeleteMapping("/posts/{postId}")
	public ResponseEntity<String> deleteMyPost(
		@PathVariable Long postId,
		@ApiIgnore @AuthenticationPrincipal CustomUserDetails userDetails) {

		if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		// UserService를 통해 PK(userId) 획득
		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());

		userService.deletePost(userId, postId);

		return ResponseEntity.ok("게시글이 삭제되었습니다.");
	}

	// 내가 쓴 댓글 조회
	@ApiOperation(value = "내가 쓴 댓글 조회")
	@GetMapping("/comments")
	public ResponseEntity<List<MyCommentDTO>> getMyComments(
		@ApiIgnore @AuthenticationPrincipal CustomUserDetails userDetails) {

		if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		// 토큰에서 ID 추출
		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());

		// 데이터 반환
		return ResponseEntity.ok(userService.getMyComments(userId));
	}

	// 내가 좋아요한 글 조회
	@ApiOperation(value = "좋아요한 게시글 조회")
	@GetMapping("/likes")
	public ResponseEntity<List<MyPostDTO>> getMyLikedPosts(
		@ApiIgnore @AuthenticationPrincipal CustomUserDetails userDetails) {

		if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());

		return ResponseEntity.ok(userService.getMyLikedPosts(userId));
	}

	// 내가 쓴 댓글 관리
	@ApiOperation(value = "내 댓글 수정", notes = "본인이 작성한 댓글 내용을 수정합니다.")
	@PatchMapping("/comments/{commentId}")
	public ResponseEntity<String> updateMyComment(
		@PathVariable Long commentId,
		@ApiIgnore @AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody CommentUpdateRequestDTO requestDTO) { // @Valid 제거

		if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		// (선택 사항) 라이브러리 대신 수동으로 유효성 검사를 하려면 아래와 같이 작성
		if (requestDTO.getContent() == null || requestDTO.getContent().trim().isEmpty()) {
			return ResponseEntity.badRequest().body("댓글 내용을 입력해주세요.");
		}

		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());
		userService.updateComment(userId, commentId, requestDTO.getContent());

		return ResponseEntity.ok("댓글이 수정되었습니다.");
	}

	@ApiOperation(value = "내 댓글 삭제", notes = "본인이 작성한 댓글을 삭제합니다.")
	@DeleteMapping("/comments/{commentId}")
	public ResponseEntity<String> deleteMyComment(
		@PathVariable Long commentId,
		@ApiIgnore @AuthenticationPrincipal CustomUserDetails userDetails) {

		if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());

		userService.deleteComment(userId, commentId);

		return ResponseEntity.ok("댓글이 삭제되었습니다.");
	}

	// 유저 정보 추가 수정
	@ApiOperation(value = "활동 지역 변경", notes = "유저의 활동 지역을 변경합니다.")
	@PatchMapping("/region")
	public ResponseEntity<String> updateRegion(
		@ApiIgnore @AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody RegionUpdateRequestDTO requestDTO) { // @Valid 제거

		if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		// 수동 검사 예시
		if (requestDTO.getRegion() == null || requestDTO.getRegion().trim().isEmpty()) {
			return ResponseEntity.badRequest().body("지역명을 입력해주세요.");
		}

		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());
		userService.updateRegion(userId, requestDTO.getRegion());

		return ResponseEntity.ok("지역 정보가 변경되었습니다.");
	}

	@ApiOperation(value = "상호명 변경", notes = "유저의 상호명을 변경합니다.")
	@PatchMapping("/store-name")
	public ResponseEntity<String> updateStoreName(
		@ApiIgnore @AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody StoreNameUpdateRequestDTO requestDTO) { // @Valid 제거

		if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		// 수동 검사 예시
		if (requestDTO.getUserStoreName() == null || requestDTO.getUserStoreName().trim().isEmpty()) {
			return ResponseEntity.badRequest().body("상호명을 입력해주세요.");
		}

		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());
		userService.updateUserStoreName(userId, requestDTO.getUserStoreName());

		return ResponseEntity.ok("상호명이 변경되었습니다.");
	}

	// 회원 탈퇴
	@ApiOperation(value = "회원 탈퇴", notes = "회원 계정을 삭제(탈퇴) 처리합니다.")
	@DeleteMapping("/withdraw")
	public ResponseEntity<String> withdrawUser(
		@ApiIgnore @AuthenticationPrincipal CustomUserDetails userDetails) {

		if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		Long userId = userService.getUserIdByProviderId(userDetails.getProviderId());

		userService.withdrawUser(userId);

		return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
	}

}