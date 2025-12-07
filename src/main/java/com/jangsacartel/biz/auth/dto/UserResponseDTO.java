package com.jangsacartel.biz.auth.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class UserResponseDTO {
	private String nickname;
	private String region;
	private String userStoreName;
	private Integer businessType;

	// DB는 Integer지만 프론트엔드엔 "0123456789" 문자열로 전달
	private String businessRegNo;

	private LocalDate businessStartDate;
}