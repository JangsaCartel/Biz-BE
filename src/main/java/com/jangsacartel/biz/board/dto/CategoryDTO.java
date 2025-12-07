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
@ApiModel(description = "카테고리 정보를 담는 DTO")
public class CategoryDTO {
	@ApiModelProperty(value = "카테고리 ID", example = "1", required = true)
	private int category_id;
	@ApiModelProperty(value = "카테고리 이름", example = "자유게시판", required = true)
	private String name;

}
