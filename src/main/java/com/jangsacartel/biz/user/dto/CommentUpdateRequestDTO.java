package com.jangsacartel.biz.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "댓글 수정 요청 DTO")
public class CommentUpdateRequestDTO {
	@ApiModelProperty(value = "수정할 댓글 내용", required = true)
	private String content;
}