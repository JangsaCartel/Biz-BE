package com.jangsacartel.biz.board.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "게시글 수정 요청 DTO")
public class PostUpdateRequestDTO {

	@ApiModelProperty(value = "수정할 제목", example = "제목 수정합니다", required = true)
	private String title;

	@ApiModelProperty(value = "수정할 내용", example = "내용을 수정했습니다.", required = true)
	private String content;

	@ApiModelProperty(value = "카테고리 ID (수정 시)", example = "2")
	private int categoryId;
}