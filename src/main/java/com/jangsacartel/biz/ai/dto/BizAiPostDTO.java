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
@ApiModel(description = "Biz-AI에 전달하는 게시글 단위 DTO")
public class BizAiPostDTO {

	@ApiModelProperty(value = "게시글 ID(문자열)", example = "123")
    private String postId;

    @ApiModelProperty(value = "게시글 제목", example = "불(fire) 대응 매뉴얼 점검")
    private String title;

    @ApiModelProperty(value = "게시글 본문", example = "불 대응 프로세스 점검. FIRE drill 실시.")
    private String content;
}
