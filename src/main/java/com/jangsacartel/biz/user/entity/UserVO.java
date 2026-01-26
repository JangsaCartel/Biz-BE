package com.jangsacartel.biz.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
	private Long userId;        // user_id (PK)
	private String nickname;    // nickname
	private String provider;    // provider (소셜 제공자)
	private String providerId;  // provider_id (소셜 고유값)
}