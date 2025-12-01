package com.jangsacartel.biz.board.dto;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 게시글 dto

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "게시글 정보를 담는 DTO")
public class BoardDTO {

	@ApiModelProperty(value = "게시글 ID", example = "1", required = true)
	private int post_id;
	@ApiModelProperty(value = "게시글 제목", example = "게시글 제목입니다.", required = true)
	private String title;
	@ApiModelProperty(value = "게시글 내용", example = "게시글 내용입니다.", required = true)
	private String content;
	@ApiModelProperty(value = "작성일", example = "2024-07-19T10:00:00.000+00:00")
	private Date created_at;
	@ApiModelProperty(value = "카테고리 ID", example = "1", required = true)
	private int category_id;
	@ApiModelProperty(value = "삭제일", example = "2024-07-19T10:00:00.000+00:00")
	private Date deleted_at;
	@ApiModelProperty(value = "수정일", example = "2024-07-19T10:00:00.000+00:00")
	private Date modified_at;
	@ApiModelProperty(value = "유저 ID", example = "1", required = true)
	private int user_id;
	
	// mapper로 계산
	@ApiModelProperty(value = "좋아요 수", example = "10")
	private int like_count;
	@ApiModelProperty(value = "댓글 수", example = "5")
	private int comment_count;
	

}
