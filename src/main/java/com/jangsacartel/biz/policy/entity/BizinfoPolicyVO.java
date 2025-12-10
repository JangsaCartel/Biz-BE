package com.jangsacartel.biz.policy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true) 
public class BizinfoPolicyVO {
	private Integer totCnt;							// 전체건수           
    private Integer inqireCo;						// 조회수

    private String rceptEngnHmpgUrl;				// 사업신청URL
    private String pblancUrl;						// 공고URL

    private String jrsdInsttNm;            			// 소기관명
    private String printFlpthNm;					// 본문출력파일경로
    private String pldirSportRealmLclasCodeNm;		// 지원분야대분류 
    private String trgetNm;							// 지원대상
    private String bsnsSumryCn;						// 사업개요내용
    private String flpthNm;							// 첨부파일경로명

    private String reqstBeginEndDe;        			// 신청기간
    private String printFileNm;						// 본문출력파일명
    private String reqstMthPapersCn;				// 사업신청방법
    private String pldirSportRealmMlsfcCodeNm;
    private String excInsttNm;						// 수행기관명
    private String refrncNm;						// 문의처
    private String pblancNm;               			// 공고명

    private String hashtags;               			// 해시태그

    private String fileNm;							// 첨부파일명
    private String creatPnttm;             			// 등록일자
    private String pblancId;   						// 공고ID
}
