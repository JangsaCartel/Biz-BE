package com.jangsacartel.biz.policy.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PolicyPagedResponseDto {
    private int page;
    private int size;
    private int totalItems;
    private int totalPages;
    private List<PolicyListItemDto> items;
}
