package com.jangsacartel.biz.auth.strategy;

import com.jangsacartel.biz.auth.dto.SocialUserInfo;

public interface SocialLoginStrategy {

	// 1. 인가 코드(AuthCode)를 주고 출입증(Access Token) 받아오기
	String getAccessToken(String authCode);

	// 2. 출입증을 보여주고 고객 정보(ID, 닉네임) 받아오기
	SocialUserInfo getUserInfo(String accessToken);

	// 3. 나는 어떤 언어(카카오, Biz 등) 담당인지 이름표 보여주기
	String getProviderName();
}