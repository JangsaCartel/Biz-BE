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
@ApiModel(description = "키워드/점수(TF-IDF)")
public class BizAiKeywordDTO {

    @ApiModelProperty(value = "키워드", example = "불")
    private String keyword;

    @ApiModelProperty(value = "TF-IDF 기반 점수)", example = "24")
    private int score;
}
