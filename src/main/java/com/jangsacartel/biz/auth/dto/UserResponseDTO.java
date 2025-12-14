package com.jangsacartel.biz.auth.dto;

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
@ApiModel(description = "로그인 사용자 정보 응답 DTO")
public class UserResponseDTO {
	@ApiModelProperty(
            value = "닉네임",
            example = "익명01"
    )
	private String nickname;
	
	@ApiModelProperty(
            value = "지역",
            example = "서울 강남구"
    )
	private String region;
	
	@ApiModelProperty(
            value = "상호명",
            example = "우리가게"
    )
	private String userStoreName;
	
	@ApiModelProperty(
            value = "사업자 등록번호 10자리(문자열)",
            example = "0000000000"
    )
	private Integer businessType;

	// DB는 Integer지만 프론트엔드엔 "0123456789" 문자열로 전달
	@ApiModelProperty(
            value = "개업일자 (YYYY-MM-DD)",
            example = "1999-01-01"
    )
	private String businessRegNo;

	private LocalDate businessStartDate;
}