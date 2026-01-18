package com.jangsacartel.biz.user.mapper;

import java.util.List;

import com.jangsacartel.biz.user.dto.MyCommentDTO;
import com.jangsacartel.biz.user.dto.MyPageProfileResponseDTO;
import com.jangsacartel.biz.user.dto.MyPostDTO;
import com.jangsacartel.biz.user.entity.UserVO;
import com.jangsacartel.biz.user.entity.UserInfoVO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
	String getTime();

	// 1. 로그인 체크용 (User 테이블만 조회해도 됨)
	UserVO findByProviderAndProviderId(@Param("provider") String provider,
		@Param("providerId") String providerId);

	// 2. 기본 회원 저장 (User 테이블)
	void insertUser(UserVO user);

	// 3. 사업자 정보 저장 (User_Info 테이블)
	void insertUserInfo(UserInfoVO userInfo);

	// 상세 정보 조회
	UserInfoVO findUserInfoByUserId(Long userId);

	// 기본 정보 조회 (닉네임 등)
	UserVO findUserById(Long userId);

	// 사업자 번호 중복 확인 (파라미터는 int)
	boolean existsByBusinessRegNo(Integer businessRegNo);

	// 마이페이지 프로필 조회
	MyPageProfileResponseDTO findProfileByUserId(Long userId);

	// 마이페이지 닉네임 수정
	void updateNickname(@Param("userId") Long userId, @Param("nickname") String nickname);

	// 마이페이지 내가 쓴 게시글 조회
	List<MyPostDTO> findPostsByUserId(Long userId);

	// 마이페이지 게시글 삭제
	int deletePost(@Param("postId") Long postId, @Param("userId") Long userId);

	// 내가 쓴 댓글
	List<MyCommentDTO> findCommentsByUserId(Long userId);

	// 내가 좋아요한 글
	List<MyPostDTO> findLikedPostsByUserId(Long userId);

	// 내가 쓴 댓글 수정
	void updateComment(@Param("commentId") Long commentId, @Param("userId") Long userId, @Param("content") String content);

	// 내가 쓴 댓글 삭제 (Soft Delete)
	void deleteComment(@Param("commentId") Long commentId, @Param("userId") Long userId);

	// 활동 지역 변경
	void updateRegion(@Param("userId") Long userId, @Param("region") String region);

	// 상호명 변경
	void updateUserStoreName(@Param("userId") Long userId, @Param("userStoreName") String userStoreName);

	// 회원 탈퇴
	// 회원이 쓴 게시글 모두 삭제
	void deletePostsByUserId(Long userId);
	// 회원이 쓴 댓글 모두 삭제
	void deleteCommentsByUserId(Long userId);
	// 회원이 누른 좋아요 기록 삭제
	void deleteLikesByUserId(Long userId);
	// 회원 탈퇴
	void withdrawUser(Long userId);

	// 게시글 ID로 관련 댓글 모두 삭제
	void deleteCommentsByPostId(Long postId);

	// 기존 게시글 삭제 메서드 (참고용)
	void deleteMyPost(Long postId);
}
