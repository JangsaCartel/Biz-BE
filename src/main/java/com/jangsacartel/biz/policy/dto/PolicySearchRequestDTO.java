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
@ApiModel(description = "정책 검색 조건 DTO (서비스 내부용)")
public class PolicySearchRequestDTO {

	@ApiModelProperty(value = "도메인 라벨", example = "창업")
    private String domainLabel;

	@ApiModelProperty(value = "해시태그 목록", example = "[\"금융\",\"서울\"]")
    private List<String> hashtags;

    @Builder.Default
    @ApiModelProperty(value = "페이지 인덱스", example = "1")
    private Integer pageIndex = 1;

    @Builder.Default
    @ApiModelProperty(value = "페이지당 개수", example = "4")
    private Integer pageUnit = 4;

    @Builder.Default
    @ApiModelProperty(value = "검색 최대 개수", example = "100")
    private Integer searchCnt = 100;
}
