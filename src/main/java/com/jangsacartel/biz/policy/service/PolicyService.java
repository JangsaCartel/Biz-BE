package com.jangsacartel.biz.policy.service;

import com.jangsacartel.biz.policy.dto.PolicyPagedResponseDTO;
import com.jangsacartel.biz.policy.dto.PolicySearchRequestDTO;

public interface PolicyService {

	PolicyPagedResponseDTO searchPolicies(PolicySearchRequestDTO cond);
}
