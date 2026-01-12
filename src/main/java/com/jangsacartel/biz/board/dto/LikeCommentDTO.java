package com.jangsacartel.biz.board.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "댓글 좋아요 정보를 담는 DTO")
public class LikeCommentDTO {
	
	@ApiModelProperty(value = "댓글 ID", example = "1", required = true)
	private int commentId;
	@ApiModelProperty(value = "게시글 ID", example = "1", required = true)
	private int postId;
	@ApiModelProperty(value = "유저 ID", example = "1", required = true)
	private int userId;
	
}
