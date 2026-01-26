package com.jangsacartel.biz.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "마이페이지 프로필 조회 응답")
public class MyPageProfileResponseDTO {

	@ApiModelProperty(value = "유저 ID (PK)", example = "1")
	private Long userId;

	@ApiModelProperty(value = "닉네임", example = "장사왕")
	private String nickname;

	@ApiModelProperty(value = "가게 상호명", example = "대박난가게")
	private String userStoreName;

	@ApiModelProperty(value = "지역", example = "충남 부여")
	private String region;
}