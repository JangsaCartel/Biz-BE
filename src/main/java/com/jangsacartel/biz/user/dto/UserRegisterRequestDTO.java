package com.jangsacartel.biz.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel(description = "회원가입 추가 정보 요청 DTO (닉네임 + 사업자 정보)")
public class UserRegisterRequestDTO {
	// 1. User 테이블용
	@ApiModelProperty(value = "닉네임", example = "익명01", required = true)
	private String nickname;

	// 2. User_Info 테이블용 (사업자 정보)
	@ApiModelProperty(value = "지역", example = "서울 강남구", required = true)
	private String region;           // 지역
	
    @ApiModelProperty(value = "상호명", example = "우리가게", required = true)
	private String userStoreName;    // 상호명
	
    @ApiModelProperty(value = "업종 코드 (예: 1=외식업, 2=카페업 등)", example = "1", required = true)
    private Integer businessType;    // 업종 코드
	
    @ApiModelProperty(value = "사업자 등록번호", example = "0000000000", required = true)
    private Integer businessRegNo;   // 사업자 등록번호
	
    @ApiModelProperty(
            value = "개업일자 (YYYY-MM-DD)",
            example = "1999-01-01",
            required = true
    )
    private LocalDate businessStartDate; // 개업일자
}