package com.jangsacartel.biz.user.service;

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

	// 게시글 삭제
	@Transactional
	public void deletePost(Long userId, Long postId) {
		userMapper.deletePost(postId, userId);
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

}