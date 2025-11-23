package com.jangsacartel.biz.board.dto;

import java.util.Date;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {
	private int comment_id;
	private int post_id;
	private int parent_comment_id;
	private String content;
	private Date created_at;
	private Date modified_at;
	private Date deleted_at;

}
