package com.jangsacartel.biz.ai.dto;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "AI 답변 생성 스트리밍 요청")
public class AiChatAnswerStreamRequestDto {

    @ApiModelProperty(value = "현재 노드 ID", required = true, example = "POLICY_NEED")
    private String nodeId;

    @ApiModelProperty(value = "프롬프트 키", required = true, example = "POLICY_NEED")
    private String promptKey;

    @ApiModelProperty(
        value = "슬롯(동적 키/값)",
        example = "{\"industry\":\"카페\",\"region\":\"서울\",\"stage\":\"운영 중\",\"userId\":\"1\"}"
    )
    private Map<String, Object> slots;

    @ApiModelProperty(value = "선택 경로(trail)")
    private List<TrailDto> trail;
}
