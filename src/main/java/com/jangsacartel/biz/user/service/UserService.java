package com.jangsacartel.biz.user.service;

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
		// 1. 게시글 삭제 시도 (Soft Delete)
		// user_id 조건이 있어 본인 글이 아니면 삭제되지 않고 0을 반환함
		int deletedCount = userMapper.deletePost(postId, userId);

		// 2. 게시글이 정상적으로 삭제된 경우(본인 글)에만 댓글 삭제 수행
		if (deletedCount > 0) {
			userMapper.deleteCommentsByPostId(postId);
		}
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

	// 내 댓글 수정
	@Transactional
	public void updateComment(Long userId, Long commentId, String content) {
		userMapper.updateComment(commentId, userId, content);
	}

	// 내 댓글 삭제
	@Transactional
	public void deleteComment(Long userId, Long commentId) {
		userMapper.deleteComment(commentId, userId);
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

	// 회원 탈퇴 (게시글, 댓글, 좋아요, 유저 정보 일괄 삭제)
	@Transactional
	public void withdrawUser(Long userId) {
		// 1. 회원이 작성한 댓글 모두 삭제
		userMapper.deleteCommentsByUserId(userId);

		// 2. 회원이 작성한 게시글 모두 삭제
		userMapper.deletePostsByUserId(userId);

		// 3. 회원이 누른 좋아요 내역 삭제
		userMapper.deleteLikesByUserId(userId);

		// 4. 회원 정보 삭제 (Soft Delete - 계정 비활성화)
		userMapper.withdrawUser(userId);
	}

	@Transactional
	public void deleteMyPost(Long userId, Long postId) {
		// 1. 게시글 존재 여부 및 본인 확인 (기존 로직 유지)
		// Post post = userMapper.findPostById(postId);
		// if (post == null || !post.getUserId().equals(userId)) { ... }

		// 2. [추가] 해당 게시글에 달린 댓글 먼저 모두 삭제
		userMapper.deleteCommentsByPostId(postId);

		// 3. 게시글 삭제 (기존 로직)
		userMapper.deleteMyPost(postId);
	}
}