package com.jangsacartel.biz.ai.dto;

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
@ApiModel(description = "핫 키워드 항목(score/freq)")
public class BizAiKeywordDTO {

    @ApiModelProperty(value = "키워드", example = "불")
    private String keyword;

    @ApiModelProperty(value = "TF-IDF 기반 점수(정수로 반올림)", example = "24")
    private int score;

    @ApiModelProperty(value = "빈도(주차 전체에서 등장 횟수)", example = "18")
    private int freq;
}
