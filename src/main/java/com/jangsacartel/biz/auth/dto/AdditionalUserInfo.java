package com.jangsacartel.biz.auth.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AdditionalUserInfo {
	// User 테이블용
	private String nickname;

	// User_Info 테이블용 (사업자 정보)
	private String region;
	private String userStoreName;
	private Integer businessType;
	private Integer businessRegNo;
	private LocalDate businessStartDate;
}