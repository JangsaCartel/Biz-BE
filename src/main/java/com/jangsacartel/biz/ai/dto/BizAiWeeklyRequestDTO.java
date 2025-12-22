package com.jangsacartel.biz.ai.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "주차별 분석 요청(BE -> Biz-AI)")
public class BizAiWeeklyRequestDTO {

    @ApiModelProperty(value = "주차 라벨", example = "2025년 12월 2주차")
    private String weekLabel;

    @ApiModelProperty(value = "상위 키워드 개수", example = "10")
    private int topK;

    @ApiModelProperty(value = "주차별 게시글 목록(최소 title/content)", required = true)
    private List<BizAiPostDTO> posts;
}