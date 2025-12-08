package com.jangsacartel.biz.policy.controller;

import com.jangsacartel.biz.policy.dto.PolicyListItemDto;
import com.jangsacartel.biz.policy.dto.PolicyPagedResponseDto;
import com.jangsacartel.biz.policy.dto.PolicySearchRequestDto;
import com.jangsacartel.biz.policy.service.PolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class PolicyController {

    private final PolicyService policyService;

    @GetMapping("/policies")
    public ResponseEntity<?> listPolicies(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) String hashtags
    ) {

    	// 쿼리 파라미터를 서비스 계층에서 사용하는 검색 DTO로 변환
        PolicySearchRequestDto cond = PolicySearchRequestDto.builder()
                .pageIndex(page)
                .pageUnit(size)
                .domainLabel(domain)
                .hashtags(hashtags != null ? Arrays.asList(hashtags.split(",")) : Collections.emptyList())
                .build();

     // 외부 Bizinfo API 호출 + 결과 가공은 서비스에서 처리
        PolicyPagedResponseDto result = policyService.searchPolicies(cond);
        
        return ResponseEntity.ok(result);
    }


}
