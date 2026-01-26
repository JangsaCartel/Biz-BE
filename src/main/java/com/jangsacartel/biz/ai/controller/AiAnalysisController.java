package com.jangsacartel.biz.ai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jangsacartel.biz.ai.dto.BizAiWeeklyResponseDTO;
import com.jangsacartel.biz.ai.enums.WeekPreset;
import com.jangsacartel.biz.ai.service.AiAnalysisService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Api(tags = "AI 분석 컨트롤러")
public class AiAnalysisController {

    private final AiAnalysisService aiAnalysisService;

    @ApiOperation(value = "주차별 HOT 키워드/워드클라우드 분석")
    @GetMapping("/ai/analysis/weekly")
    public ResponseEntity<BizAiWeeklyResponseDTO> weekly(
        @ApiParam(value = "주차 프리셋(THIS/LAST). 미지정 시 LAST", required = false, example = "LAST")
        @RequestParam(value = "week", required = false, defaultValue = "LAST")
        WeekPreset week,

        @ApiParam(value = "Top K 키워드 개수", required = false, example = "10")
        @RequestParam(value = "topK", required = false, defaultValue = "10")
        int topK,

        @ApiParam(value = "DB에서 가져올 게시글 최대 개수", required = false, example = "500")
        @RequestParam(value = "limit", required = false, defaultValue = "500")
        int limit
    ) {
        BizAiWeeklyResponseDTO res = aiAnalysisService.getWeeklyAnalysis(week, topK, limit);
        return ResponseEntity.ok(res);
    }
}
