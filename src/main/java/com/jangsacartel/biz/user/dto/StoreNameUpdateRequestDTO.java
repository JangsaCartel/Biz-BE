package com.jangsacartel.biz.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "상호명 변경 요청 DTO")
public class StoreNameUpdateRequestDTO {
	@ApiModelProperty(value = "변경할 상호명", example = "장사왕김사장", required = true)
	private String userStoreName;
}