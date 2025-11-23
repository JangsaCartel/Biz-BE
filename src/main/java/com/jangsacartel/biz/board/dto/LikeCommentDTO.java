package com.jangsacartel.biz.board.dto;

import lombok.AllArgsConstructor;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeCommentDTO {
	
	private int comment_id;
	private int post_id;
	private int user_id;
	
}
