package com.jangsacartel.biz.auth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel(description = "회원 추가 정보 DTO")
public class AdditionalUserInfoDTO {
	// User 테이블용
	@ApiModelProperty(value = "닉네임", example = "익명01")
	private String nickname;

	// User_Info 테이블용 (사업자 정보)
	@ApiModelProperty(value = "시/도", example = "서울", required = true)
	private String city;

	@ApiModelProperty(value = "시/군/구", example = "강남구", required = true)
	private String district;

	@ApiModelProperty(value = "읍/면/동", example = "역삼동", required = true)
	private String neighborhood;
	
	@ApiModelProperty(value = "상호명", example = "우리가게")
	private String userStoreName;
	
	@ApiModelProperty(value = "업종 코드 (예: 1=외식업, 2=카페업 등)", example = "1", required = true)
	private Integer businessType;
	
	@ApiModelProperty(value = "사업자 등록번호", example = "0000000000", required = true)
    private Integer businessRegNo;
	
	@ApiModelProperty(
            value = "개업일자 (YYYY-MM-DD)",
            example = "1999-01-01",
            required = true
    )
	private LocalDate businessStartDate;
}