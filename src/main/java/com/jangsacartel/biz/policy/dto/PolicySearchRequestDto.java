package com.jangsacartel.biz.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicySearchRequestDto {

    private String domainLabel;

    private List<String> hashtags;

    @Builder.Default
    private Integer pageIndex = 1;

    @Builder.Default
    private Integer pageUnit = 4;

    @Builder.Default
    private Integer searchCnt = 100;
}
