package com.jangsacartel.biz.board.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 게시글 dto

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDTO {

	private int post_id;
	private String title;
	private String content;
	private Date created_at;
	private int category_id;
	private Date deleted_at;
	private Date modified_at;
	private int user_id;
	
	// mapper로 계산
	private int like_count;
	private int comment_count;
	

}
