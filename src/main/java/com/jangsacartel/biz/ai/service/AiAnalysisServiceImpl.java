package com.jangsacartel.biz.ai.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.jangsacartel.biz.ai.dto.BizAiPostDTO;
import com.jangsacartel.biz.ai.dto.BizAiWeeklyRequestDTO;
import com.jangsacartel.biz.ai.dto.BizAiWeeklyResponseDTO;
import com.jangsacartel.biz.ai.dto.PostLiteDTO;
import com.jangsacartel.biz.ai.enums.WeekPreset;
import com.jangsacartel.biz.ai.mapper.AiMapper;
import com.jangsacartel.biz.ai.util.WeekLabelUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiAnalysisServiceImpl implements AiAnalysisService {

    private final AiMapper aiMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${biz.ai.base-url}")
    private String baseUrl;

    @Value("${biz.ai.api-key}")
    private String apiKey;

    @Override
    public BizAiWeeklyResponseDTO getWeeklyAnalysis(WeekPreset weekPreset, int limit, int topK) {

        // 1) WeekPreset → start/end 계산 (주 시작: 월요일, end는 exclusive)
        LocalDate today = LocalDate.now();

        LocalDate thisWeekStart = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate thisWeekEnd = thisWeekStart.plusDays(7);

        LocalDate start;
        LocalDate end;

        switch (weekPreset) {
            case THIS:
                start = thisWeekStart;
                end = thisWeekEnd;
                break;
            case LAST:
            default:
                start = thisWeekStart.minusDays(7);
                end = thisWeekStart;
                break;
        }

        // 2) 기존 로직(LocalDate start/end 버전)으로 위임
        return getWeeklyAnalysis(start, end, topK, limit);
    }

    public BizAiWeeklyResponseDTO getWeeklyAnalysis(LocalDate start, LocalDate end, int topK, int limit) {

        // 기간 범위: start inclusive, end exclusive
        LocalDateTime startDt = start.atStartOfDay();
        LocalDateTime endDt = end.atStartOfDay();

        List<PostLiteDTO> posts = aiMapper.selectPostsByDateRange(
            Timestamp.valueOf(startDt),
            Timestamp.valueOf(endDt),
            limit
        );

        String weekLabel = WeekLabelUtil.buildLabel(start);

        // 데이터가 없으면 AI 서버 호출하지 않고 기본 응답
        if (posts == null || posts.size() < 10) {
            BizAiWeeklyResponseDTO empty = new BizAiWeeklyResponseDTO();
            empty.setWeekLabel(weekLabel);
            empty.setTopKeywords(Collections.emptyList());
            empty.setWordcloudPngBase64(null);
            return empty;
        }

        List<BizAiPostDTO> mappedPosts = posts.stream()
            .map(p -> BizAiPostDTO.builder()
                .postId(p.getPostId() == null ? null : String.valueOf(p.getPostId()))
                .title(p.getTitle())
                .content(p.getContent())
                .build())
            .collect(Collectors.toList());

        BizAiWeeklyRequestDTO req = BizAiWeeklyRequestDTO.builder()
            .weekLabel(weekLabel)
            .topK(topK)
            .posts(mappedPosts)
            .build();

        String url = baseUrl + "/analysis/weekly";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-AI-KEY", apiKey);

        HttpEntity<BizAiWeeklyRequestDTO> entity = new HttpEntity<>(req, headers);

        try {
            return restTemplate.postForObject(url, entity, BizAiWeeklyResponseDTO.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Biz-AI weekly analysis call failed: " + e.getMessage(), e);
        }
    }
}
