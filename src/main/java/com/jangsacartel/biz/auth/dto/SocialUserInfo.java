package com.jangsacartel.biz.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SocialUserInfo {
	private String providerId; // 소셜 쪽의 고유 ID (식별자)
	private String nickname;   // (선택) 닉네임
	private String email;      // (선택) 이메일
}