package com.jangsacartel.biz.user.mapper;

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
}
