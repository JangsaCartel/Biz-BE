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
@ApiModel(description = "파일 정보를 담는 DTO")
public class FileDTO {
	
	@ApiModelProperty(value = "파일 ID", example = "1", required = true)
	private int file_id;
	@ApiModelProperty(value = "게시글 ID", example = "1", required = true)
	private int post_id;
	@ApiModelProperty(value = "파일 URL", example = "https://example.com/file.jpg", required = true)
	private String url;

}
