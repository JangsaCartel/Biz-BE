package com.jangsacartel.biz.policy.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel(description = "정책 목록 페이징 응답 DTO")
public class PolicyPagedResponseDto {

    @ApiModelProperty(value = "현재 페이지 번호 (1부터 시작)", example = "1")
    private int page;
    
    @ApiModelProperty(value = "페이지당 항목 수", example = "4")
    private int size;
    
    @ApiModelProperty(value = "전체 항목 수", example = "1460")
    private int totalItems;
    
    @ApiModelProperty(value = "전체 페이지 수", example = "365")
    private int totalPages;
    
    @ApiModelProperty(value = "정책 목록")
    private List<PolicyListItemDto> items;
}
