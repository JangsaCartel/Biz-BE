package com.jangsacartel.biz.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVO {
	private Long userId;             // user_id (PK & FK)
	private String region;           // region
	private String userStoreName;    // user_storeName
	private Integer businessType;    // business_type
	private Integer businessRegNo;   // business_reg_no
	private LocalDate businessStartDate; // business_start_date_
}