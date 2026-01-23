package com.jangsacartel.biz.ai.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "AI 자유 질문 스트리밍 요청")
public class AiChatLlmStreamRequestDto {

    @ApiModelProperty(value = "자유 질문 텍스트", required = true, example = "안녕, 오늘 인기 정책 알려줘")
    private String text;
}
