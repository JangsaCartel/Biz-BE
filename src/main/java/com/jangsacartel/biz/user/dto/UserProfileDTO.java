package com.jangsacartel.biz.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "마이페이지 프로필 조회 응답 DTO (보안 필드 제외)")
public class UserProfileDTO {

	@ApiModelProperty(value = "닉네임", example = "익명01")
	private String nickname;

	@ApiModelProperty(value = "지역", example = "서울 강남구")
	private String region;

	@ApiModelProperty(value = "상호명", example = "우리가게")
	private String userStoreName;

	@ApiModelProperty(value = "사업자 종류", example = "1")
	private Integer businessType;

	@ApiModelProperty(value = "사업자 등록번호", example = "0000000000")
	private String businessRegNo;

	@ApiModelProperty(value = "개업일자", example = "1999-01-01")
	private LocalDate businessStartDate;
}