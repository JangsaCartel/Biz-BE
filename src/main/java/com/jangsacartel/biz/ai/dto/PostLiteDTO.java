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
@ApiModel(description = "DB에서 조회한 게시글 최소 정보 DTO")
public class PostLiteDTO {
	
	 	@ApiModelProperty(value = "게시글 ID", example = "123")
	    private Long postId;

	    @ApiModelProperty(value = "게시글 제목", example = "Gemini 2.5 Flash 연결 이슈")
	    private String title;

	    @ApiModelProperty(value = "게시글 본문", example = "응답 지연 발생. 캐싱 전략 점검 필요.")
	    private String content;
}