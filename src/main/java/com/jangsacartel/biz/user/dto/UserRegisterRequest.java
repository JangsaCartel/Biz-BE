package com.jangsacartel.biz.user.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRegisterRequest {
	// 1. User 테이블용
	private String nickname;

	// 2. User_Info 테이블용 (사업자 정보)
	private String region;           // 지역
	private String userStoreName;    // 상호명
	private Integer businessType;    // 업종 코드 (예: 1=한식, 2=중식 등)
	private Integer businessRegNo;   // 사업자 등록번호
	private LocalDate businessStartDate; // 개업일자
}