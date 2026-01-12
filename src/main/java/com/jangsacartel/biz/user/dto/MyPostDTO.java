package com.jangsacartel.biz.user.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import java.util.Date;

@Data
@ApiModel(description = "내가 쓴 게시글 정보")
public class MyPostDTO {
	private Long postId;
	private String title;
	private Date createdAt;
	private String categoryName;
}