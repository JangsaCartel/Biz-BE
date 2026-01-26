package com.jangsacartel.biz.user.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "내가 쓴 게시글 정보")
public class MyPostDTO {
	private Long postId;
	private String title;
	private String content;

	private LocalDateTime createdAt;

	private String categoryName;
	private Long likeCount;
	private Long commentCount;

	private LocalDateTime modifiedAt;
}