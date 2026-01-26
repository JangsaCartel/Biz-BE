package com.jangsacartel.biz.user.dto;

import lombok.Data;
import java.util.Date;

@Data
public class MyCommentDTO {
	private Long commentId;
	private String content;   // 댓글 내용
	private Date createdAt;   // 작성일

	// 조인해서 가져올 게시글 정보
	private Long postId;
	private String postTitle;

	// 게시판 이름
	private String categoryName;
}