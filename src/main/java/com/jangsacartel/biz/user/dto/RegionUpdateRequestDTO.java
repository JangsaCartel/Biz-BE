package com.jangsacartel.biz.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "활동 지역 변경 요청 DTO")
public class RegionUpdateRequestDTO {
	@ApiModelProperty(value = "변경할 지역명", example = "서울 강남구", required = true)
	private String region;
}