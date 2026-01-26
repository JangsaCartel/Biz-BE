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

	// 1. 로그인 체크용
	UserVO findByProviderAndProviderId(@Param("provider") String provider,
		@Param("providerId") String providerId);

	// 2. 기본 회원 저장
	void insertUser(UserVO user);

	// 3. 사업자 정보 저장
	void insertUserInfo(UserInfoVO userInfo);

	// 상세 정보 조회
	UserInfoVO findUserInfoByUserId(Long userId);

	// 기본 정보 조회
	UserVO findUserById(Long userId);

	// 사업자 번호 중복 확인
	boolean existsByBusinessRegNo(Integer businessRegNo);

	// 마이페이지 프로필 조회
	MyPageProfileResponseDTO findProfileByUserId(Long userId);

	// 마이페이지 닉네임 수정
	void updateNickname(@Param("userId") Long userId, @Param("nickname") String nickname);

	// 마이페이지 내가 쓴 게시글 조회
	List<MyPostDTO> findPostsByUserId(Long userId);

	// 내가 쓴 댓글 조회
	List<MyCommentDTO> findCommentsByUserId(Long userId);

	// 내가 좋아요한 글 조회
	List<MyPostDTO> findLikedPostsByUserId(Long userId);

	// 활동 지역 변경
	void updateRegion(@Param("userId") Long userId, @Param("region") String region);

	// 상호명 변경
	void updateUserStoreName(@Param("userId") Long userId, @Param("userStoreName") String userStoreName);

	// 회원 탈퇴
	// 1. 회원이 누른 게시글 좋아요 삭제
	void deletePostLikesByUserId(Long userId);
	// 2. 회원이 누른 댓글 좋아요 삭제
	void deleteCommentLikesByUserId(Long userId);
	// 3. 회원이 작성한 댓글 삭제 (Hard Delete)
	void deleteCommentsByUserId(Long userId);
	// 4. 회원이 작성한 게시글 삭제 (Hard Delete)
	void deletePostsByUserId(Long userId);
	// 5. 회원 부가 정보 삭제 (User_Info)
	void deleteUserInfoByUserId(Long userId);
	// 6. 회원 본체 삭제 (User)
	void hardDeleteUser(Long userId);
}
