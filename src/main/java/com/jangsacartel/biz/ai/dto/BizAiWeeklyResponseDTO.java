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
@ApiModel(description = "주차별 분석 응답(Biz-AI -> BE -> FE)")
public class BizAiWeeklyResponseDTO {

    @ApiModelProperty(value = "주차 라벨", example = "2025년 12월 2주차")
    private String weekLabel;

    @ApiModelProperty(value = "상위 키워드 목록(score/freq 포함)")
    private List<BizAiKeywordDTO> topKeywords;

    @ApiModelProperty(value = "워드클라우드 base64 PNG(data URI)", example = "data:image/png;base64,iVBORw0K...")
    private String wordcloudPngBase64;
}