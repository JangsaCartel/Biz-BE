package com.jangsacartel.biz.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "정책 카드/상세에 사용되는 정책 항목 DTO")
public class PolicyListItemDto {
	// 정책 카드 한 개에 대응하는 DTO
	
	@ApiModelProperty(value = "정책 ID (pblancId)", example = "PBLN_000000000116756")
    private String id;              // pblancId

    @ApiModelProperty(value = "주관기관 · 수행기관", example = "과학기술정보통신부 · 한국정보산업연합회")
    private String organization;    // "주관기관 · 수행기관"
    
    @ApiModelProperty(value = "정책 제목 (pblancNm)", example = "2026년 ICT 학점연계 프로젝트 인턴십 사업 공고")
    private String title;           // pblancNm
    
    @ApiModelProperty(value = "접수 기간", example = "2025.12.11 ~ 2026.01.12")
    private String period;          // "YYYY.MM.DD ~ YYYY.MM.DD" 또는 "상시접수"
    
    @ApiModelProperty(value = "D-Day 표기", example = "D-31")
    private String dDay;            // "D-3", "D-DAY", "마감" 등
    
    @ApiModelProperty(value = "태그 목록", example = "[\"금융\",\"서울\"]")
    private List<String> tags;      // ["금융","서울",...]
    
    @ApiModelProperty(value = "등록일/생성일 (creatPnttm)", example = "2025-12-11 10:35:36")
    private String createdAt;       // creatPnttm
    
 // 상세페이지용 필드 추가
    
    @ApiModelProperty(value = "지원 대상", example = "중소기업")
    private String target;
    
    @ApiModelProperty(value = "문의처", example = "02-3460-3319")
    private String contact;
    
    @ApiModelProperty(value = "상세 HTML 본문", example = "<p>지원 내용...</p>")
    private String htmlContent;
    
    @ApiModelProperty(value = "신청 URL", example = "https://www.ictintern.or.kr/main.do")
    private String applyUrl;
    
    @ApiModelProperty(value = "원문 URL", example = "https://www.bizinfo.go.kr/web/lay1/bbs/S1T122C128/AS/74/view.do?pblancId=PBLN_000000000116756")
    private String originalUrl;
    
    @ApiModelProperty(value = "메인 첨부 파일명", example = "★2026년 ICT 학점연계 프로젝트 인턴십 사업 공고문.pdf")
    private String mainFileName;
    
    @ApiModelProperty(value = "메인 첨부 파일 경로/명", example = "https://www.bizinfo.go.kr/cmm/fms/getImageFile.do?atchFileId=FILE_000000000738304&fileSn=0")
    private String printFlpthNm;
    
    @ApiModelProperty(value = "추가 첨부 파일명 목록", example = "[\"2026년 ICT학점연계프로젝트인턴십 상반기 국내과정 기업 신청 서류.zip\"]")
    private List<String> extraFileNames;
    
    @ApiModelProperty(value = "추가 첨부 파일 URL 목록", example = "[\"https://www.bizinfo.go.kr/cmm/fms/getImageFile.do?atchFileId=FILE_000000000738305&fileSn=0\"]")
    private List<String> extraFileUrls;
    

}
