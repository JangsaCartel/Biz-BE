package com.jangsacartel.biz.board.dto;

import java.util.Date;

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
@ApiModel(description = "댓글 정보를 담는 DTO")
public class CommentDTO {
	@ApiModelProperty(value = "댓글 ID", example = "1", required = true)
	private int comment_id;
	@ApiModelProperty(value = "게시글 ID", example = "1", required = true)
	private int post_id;
	@ApiModelProperty(value = "부모 댓글 ID", example = "1")
	private int parent_comment_id;
	@ApiModelProperty(value = "댓글 내용", example = "댓글 내용입니다.", required = true)
	private String content;
	@ApiModelProperty(value = "작성일", example = "2024-07-19T10:00:00.000+00:00")
	private Date created_at;
	@ApiModelProperty(value = "수정일", example = "2024-07-19T10:00:00.000+00:00")
	private Date modified_at;
	@ApiModelProperty(value = "삭제일", example = "2024-07-19T10:00:00.000+00:00")
	private Date deleted_at;

}
