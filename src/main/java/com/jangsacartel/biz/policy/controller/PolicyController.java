package com.jangsacartel.biz.policy.controller;

import com.jangsacartel.biz.policy.dto.PolicyListItemDto;
import com.jangsacartel.biz.policy.dto.PolicyPagedResponseDto;
import com.jangsacartel.biz.policy.dto.PolicySearchRequestDto;
import com.jangsacartel.biz.policy.service.PolicyService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(tags = "정책 컨트롤러")
public class PolicyController {

    private final PolicyService policyService;

    @ApiOperation(
    		  value = "정책 목록 조회",
    		  notes = "정부 및 기관의 지원 정책 목록을 페이지 단위로 조회합니다.\n" +
    		            "- Bizinfo 외부 API를 기반으로 정책 데이터를 수집합니다.\n" +
    		            "- 도메인(domain) 및 해시태그(hashtags) 필터링이 가능합니다.\n" +
    		            "- hashtags는 콤마(,)로 구분합니다. 예) 청년,중소기업\n" +
    		            "- 외부 Bizinfo API 호출 및 결과 가공은 PolicyService에서 처리합니다."
    		)
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "정책 목록 조회 성공",
            response = PolicyPagedResponseDto.class
        ),
        @ApiResponse(code = 400, message = "잘못된 요청 파라미터"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found"),
        @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @GetMapping("/policies")
    public ResponseEntity<?> listPolicies(
    		@ApiParam(value = "페이지 번호 (1부터 시작)", example = "1", required = true)
            @RequestParam int page,
            
            @ApiParam(value = "페이지당 항목 수", example = "4", required = true)
            @RequestParam int size,
            
            @ApiParam(value = "정책 도메인 필터 (예: 창업, 수출)", example = "창업")
            @RequestParam(required = false) String domain,
            
            @ApiParam(
                    value = "해시태그 필터 (콤마로 구분)",
                    example = "금융, 서울"
                )
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
