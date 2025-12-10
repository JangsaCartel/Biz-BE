package com.jangsacartel.biz.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyListItemDto {
	// 정책 카드 한 개에 대응하는 DTO
	
    private String id;              // pblancId
    private String organization;    // "주관기관 · 수행기관"
    private String title;           // pblancNm
    private String period;          // "YYYY.MM.DD ~ YYYY.MM.DD" 또는 "상시접수"
    private String dDay;            // "D-3", "D-DAY", "마감" 등
    private List<String> tags;      // ["금융","서울",...]
    private String createdAt;       // creatPnttm
}
