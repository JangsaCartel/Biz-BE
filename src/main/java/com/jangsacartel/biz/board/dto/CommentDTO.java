package com.jangsacartel.biz.board.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
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
	private int commentId;
	@ApiModelProperty(value = "게시글 ID", example = "1", required = true)
	private int postId;
	@ApiModelProperty(value = "부모 댓글 ID", example = "1")
	private Integer parentCommentId;
	@ApiModelProperty(value = "댓글 내용", example = "댓글 내용입니다.", required = true)
	private String content;
	@ApiModelProperty(value = "작성일", example = "2024-07-19T10:00:00.000+00:00")
	private Date createdAt;
	@ApiModelProperty(value = "수정일", example = "2024-07-19T10:00:00.000+00:00")
	private Date modifiedAt;
	@ApiModelProperty(value = "삭제일", example = "2024-07-19T10:00:00.000+00:00")
	private Date deletedAt;
	@ApiModelProperty(value = "유저 ID", example = "1", required = true)
	private Integer userId;
	@ApiModelProperty(value = "작성자 닉네임", example = "장사꾼")
	private String nickname;
	@ApiModelProperty(value = "좋아요 수", example = "10")
	private int likeCount;
	@ApiModelProperty(value = "현재 사용자의 좋아요 여부", example = "true")
	private boolean liked;

}
