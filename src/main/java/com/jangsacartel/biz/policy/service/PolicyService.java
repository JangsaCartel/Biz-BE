package com.jangsacartel.biz.policy.service;

import com.jangsacartel.biz.policy.dto.PolicyListItemDto;
import com.jangsacartel.biz.policy.dto.PolicyPagedResponseDto;
import com.jangsacartel.biz.policy.dto.PolicySearchRequestDto;

import java.util.List;

public interface PolicyService {

	PolicyPagedResponseDto searchPolicies(PolicySearchRequestDto cond);
}
