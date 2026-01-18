package com.jangsacartel.biz.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "닉네임 수정 요청")
public class NicknameUpdateRequestDTO {

	@ApiModelProperty(value = "변경할 닉네임", example = "새로운닉네임", required = true)
	private String nickname;

}