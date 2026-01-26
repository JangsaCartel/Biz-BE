package com.jangsacartel.biz.ai.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "Ai 채팅 선택 경로 아이템")
public class TrailDto {
    @ApiModelProperty(example = "ROOT")
    private String from;

    @ApiModelProperty(example = "정책 관련")
    private String label;

    @ApiModelProperty(example = "POLICY_ROOT")
    private String to;
}
