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
@ApiModel(description = "Biz-AI 주간 분석 응답 DTO")
public class BizAiWeeklyResponseDTO {

    @ApiModelProperty(value = "주차 라벨", example = "2025년 12월 2주차")
    private String weekLabel;

    @ApiModelProperty(value = "상위 키워드 목록(score는 TF-IDF 점수)", required = true)
    private List<BizAiKeywordDTO> topKeywords;

    @ApiModelProperty(value = "워드클라우드(data URI)", example = "data:image/png;base64,iVBORw0K...")
    private String wordcloudPngBase64;
}