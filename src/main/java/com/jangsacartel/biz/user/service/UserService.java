package com.jangsacartel.biz.user.service;

import com.jangsacartel.biz.board.service.BoardService;
import com.jangsacartel.biz.board.service.CommentService;
import com.jangsacartel.biz.user.dto.MyCommentDTO;
import com.jangsacartel.biz.user.dto.MyPageProfileResponseDTO;
import com.jangsacartel.biz.user.dto.MyPostDTO;
import com.jangsacartel.biz.user.entity.UserVO;
import com.jangsacartel.biz.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserMapper userMapper;
	private final BoardService boardService;
	private final CommentService commentService;

	// 프로필 조회
	@Transactional(readOnly = true)
	public MyPageProfileResponseDTO getMyPageProfile(Long userId) {
		return userMapper.findProfileByUserId(userId);
	}

	// 닉네임 수정
	@Transactional
	public void updateNickname(Long userId, String newNickname) {
		userMapper.updateNickname(userId, newNickname);
	}

	// 내 게시글 목록 조회
	@Transactional(readOnly = true)
	public List<MyPostDTO> getMyPosts(Long userId) {
		return userMapper.findPostsByUserId(userId);
	}

	// 게시글 삭제 (댓글 삭제 포함)
	@Transactional
	public void deletePost(Long userId, Long postId) {
		boardService.deletePost(postId.intValue(), userId.intValue());
	}

	// ProviderId(소셜 ID)로 User ID(DB PK) 조회
	@Transactional(readOnly = true)
	public Long getUserIdByProviderId(String providerId) {
		// "kakao"는 고정값이거나 파라미터로 받을 수 있다.
		// 현재 로직상 KakaoAuthService가 "kakao"로 저장하므로 "kakao"로 조회합니다.
		UserVO user = userMapper.findByProviderAndProviderId("kakao", providerId);
		if (user == null) {
			throw new IllegalArgumentException("존재하지 않는 회원입니다.");
		}
		return user.getUserId();
	}

	// 내가 쓴 댓글 가져오기
	@Transactional(readOnly = true)
	public List<MyCommentDTO> getMyComments(Long userId) {
		return userMapper.findCommentsByUserId(userId);
	}

	// 내가 좋아요한 글 가져오기
	@Transactional(readOnly = true)
	public List<MyPostDTO> getMyLikedPosts(Long userId) {
		return userMapper.findLikedPostsByUserId(userId);
	}

	// 댓글 수정
	@Transactional
	public void updateComment(Long userId, Long commentId, String content) {
		commentService.updateComment(commentId.intValue(), userId.intValue(), content);
	}

	// 댓글 삭제
	@Transactional
	public void deleteComment(Long userId, Long commentId) {
		commentService.deleteComment(commentId.intValue(), userId.intValue());
	}

	// 활동 지역 변경
	@Transactional
	public void updateRegion(Long userId, String region) {
		userMapper.updateRegion(userId, region);
	}

	// 상호명 변경
	@Transactional
	public void updateUserStoreName(Long userId, String userStoreName) {
		userMapper.updateUserStoreName(userId, userStoreName);
	}

	// 회원 탈퇴 (Hard Delete: 영구 삭제)
	@Transactional
	public void withdrawUser(Long userId) {
		// 1. 회원이 누른 좋아요(게시글/댓글) 삭제
		userMapper.deletePostLikesByUserId(userId);
		userMapper.deleteCommentLikesByUserId(userId);

		// 2. 회원이 작성한 댓글 삭제
		userMapper.deleteCommentsByUserId(userId);

		// 3. 회원이 작성한 게시글 삭제
		// (DB FK 설정에 따라 게시글 삭제 시 게시글에 달린 댓글/좋아요도 함께 삭제되길 기대하거나, 별도 처리가 필요할 수 있음)
		userMapper.deletePostsByUserId(userId);

		// 4. 회원 상세 정보(User_Info) 삭제
		userMapper.deleteUserInfoByUserId(userId);

		// 5. 회원 기본 정보(User) 영구 삭제
		userMapper.hardDeleteUser(userId);
	}

}