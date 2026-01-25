package com.jangsacartel.biz.policy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jangsacartel.biz.policy.dto.PolicyListItemDTO;
import com.jangsacartel.biz.policy.dto.PolicyPagedResponseDTO;
import com.jangsacartel.biz.policy.dto.PolicySearchRequestDTO;
import com.jangsacartel.biz.policy.entity.BizinfoPolicyResponseVO;
import com.jangsacartel.biz.policy.entity.BizinfoPolicyVO;
import com.jangsacartel.biz.policy.enums.PolicyDomain;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class PolicyServiceImpl implements PolicyService {

	// Bizinfo 응답 JSON 파싱용 ObjectMapper
    private final ObjectMapper objectMapper = new ObjectMapper();
    // 외부 HTTP 호출용 RestTemplate
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${bizinfo.api.key}")
    private String bizinfoApiKey;

    private static final String BIZINFO_ORIGIN = "https://www.bizinfo.go.kr";
    private static final String BIZINFO_API_URL =
            "https://www.bizinfo.go.kr/uss/rss/bizinfoApi.do";

    // 정책 목록 조회
    @Override
    public PolicyPagedResponseDTO searchPolicies(PolicySearchRequestDTO cond) {

    	// 1. 분야 라벨("내수 분야" 등)을 Bizinfo에서 요구하는 코드값(searchLclasId)로 변환
        PolicyDomain domain = PolicyDomain.fromLabel(cond.getDomainLabel());
        String searchLclasId = domain.getCode();

        // 2. 해시태그 리스트 → "금융,서울" 형태의 쿼리 파라미터 문자열로 변환
        String hashtagsParam = (cond.getHashtags() != null && !cond.getHashtags().isEmpty())
                ? String.join(",", cond.getHashtags())
                : null;

        // 3. Bizinfo API URL 구성	
        String url = BIZINFO_API_URL
                + "?crtfcKey=" + bizinfoApiKey
                + "&dataType=json"
                + "&pageUnit=" + cond.getPageUnit()
                + "&pageIndex=" + cond.getPageIndex();

        if (searchLclasId != null && !searchLclasId.isBlank()) {
            url += "&searchLclasId=" + searchLclasId;
        }
        if (hashtagsParam != null) {
            url += "&hashtags=" + hashtagsParam;
        }

        // 4. 외부 API 호출 후 Raw JSON 문자열 수신
        String rawJson = restTemplate.getForObject(url, String.class);

        // 5. JSON → BizinfoPolicyResponseVO 역직렬화
        BizinfoPolicyResponseVO response;
        try {
            response = objectMapper.readValue(rawJson, BizinfoPolicyResponseVO.class);
        } catch (Exception e) {
        	// 파싱에 실패하면 빈 페이지를 반환
            log.error("JSON 파싱 오류", e);
            return PolicyPagedResponseDTO.builder()
                    .page(cond.getPageIndex())
                    .size(cond.getPageUnit())
                    .totalItems(0)
                    .totalPages(0)
                    .items(Collections.emptyList())
                    .build();
        }

     // 응답 본문이 없을 시 빈 페이지 반환
        if (response == null || response.getJsonArray() == null) {
            return PolicyPagedResponseDTO.builder()
                    .page(cond.getPageIndex())
                    .size(cond.getPageUnit())
                    .totalItems(0)
                    .totalPages(0)
                    .items(Collections.emptyList())
                    .build();
        }

        // 6. Bizinfo 원본 VO 리스트 → FE 전용 DTO 리스트로 변환
        List<PolicyListItemDTO> items = response.getJsonArray().stream()
                .map(this::toListItemDto)
                .collect(Collectors.toList());

        // Bizinfo 특성상 각 row 에 totCnt 가 중복 포함되어 있어, 첫 번째 요소에서만 읽어서 사용
        int totalCnt = response.getJsonArray().isEmpty()
                ? 0
                : response.getJsonArray().get(0).getTotCnt(); // Bizinfo API 형식

        // 페이지 수 계산 (올림 처리)
        int totalPages = (int) Math.ceil((double) totalCnt / cond.getPageUnit());

        // 7. 프론트에서 바로 사용하는 페이징 응답 DTO 생성
        return PolicyPagedResponseDTO.builder()
                .page(cond.getPageIndex())
                .size(cond.getPageUnit())
                .totalItems(totalCnt)
                .totalPages(totalPages)
                .items(items)
                .build();
    }
    

    // Bizinfo 한 건(BizinfoPolicyVO)을 프론트 카드 한 장(PolicyListItemDTO)으로 변환.
    private PolicyListItemDTO toListItemDto(BizinfoPolicyVO vo) {

        String organization = (vo.getExcInsttNm() != null && !vo.getExcInsttNm().isEmpty())
                ? vo.getJrsdInsttNm() + " · " + vo.getExcInsttNm()
                : vo.getJrsdInsttNm();

        return PolicyListItemDTO.builder()
                .id(vo.getPblancId())
                .organization(organization)
                .title(vo.getPblancNm())
                .period(formatDateRange(vo.getReqstBeginEndDe()))
                .dDay(calcDDay(vo.getReqstBeginEndDe()))
                .tags(splitTags(vo.getHashtags()))
                .createdAt(vo.getCreatPnttm())
                
                .target(vo.getTrgetNm())
                .contact(vo.getRefrncNm())
                .htmlContent(vo.getBsnsSumryCn())
                .applyUrl(vo.getRceptEngnHmpgUrl())
                .originalUrl(BIZINFO_ORIGIN + vo.getPblancUrl())
                .mainFileName(vo.getPrintFileNm())
                .printFlpthNm(vo.getPrintFlpthNm())
                .extraFileNames(splitFileNames(vo.getFileNm()))
                .extraFileUrls(splitFileNames(vo.getFlpthNm()))
                
                .build();
    }

    // Bizinfo에서 내려오는 파일명/파일경로 문자열을 리스트로 변환. "file1@file2@file3" 형태를 분리.
    private List<String> splitFileNames(String fileNm) {
        if (fileNm == null || fileNm.isBlank()) {
            return Collections.emptyList();
        }

        return Arrays.stream(fileNm.split("@"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    // "20251126 ~ 20251231" 형태의 문자열을 "2025.11.26 ~ 2025.12.31" 와 같이 표시용 포맷으로 변환. 형식이 맞지 않으면 "상시접수" 로 처리한다.
    private String formatDateRange(String ymdRange) {
        if (ymdRange == null || ymdRange.isBlank()) return "상시접수";

        if (!ymdRange.contains("~")) return "상시접수";

        String[] parts = ymdRange.split("~");
        if (parts.length != 2) return "상시접수";

        return formatYyyyMmDd(parts[0].trim())
                + " ~ "
                + formatYyyyMmDd(parts[1].trim());
    }

    // "YYYYMMDD" → "YYYY.MM.DD" 로 변환.
    private String formatYyyyMmDd(String raw) {
        if (raw == null || raw.length() != 8) return raw;

        return raw.substring(0, 4) + "."
                + raw.substring(4, 6) + "."
                + raw.substring(6, 8);
    }

    // 접수기간 문자열(예: "20251126 ~ 20251231")에서 마감일을 읽어 오늘 기준으로 남은 일수를 계산하고 D-Day 텍스트로 반환.
    // 마감일이 지났으면 "마감", 오늘이 마감일이면 "D-DAY", 그 외에는 "D-n", 기간 정보가 없거나 형식이 맞지 않으면 "상시"
    private String calcDDay(String ymdRange) {
        if (ymdRange == null || !ymdRange.contains("~")) return "상시";

        String endRaw = ymdRange.split("~")[1].trim();

        if (endRaw.length() != 8) return "상시";

        LocalDate end = LocalDate.parse(endRaw, DateTimeFormatter.BASIC_ISO_DATE);
        long diff = ChronoUnit.DAYS.between(LocalDate.now(), end);

        if (diff < 0) return "마감";
        if (diff == 0) return "D-DAY";

        return "D-" + diff;
    }

    // Bizinfo에서 내려오는 해시태그 문자열을 리스트로 변환.
    private List<String> splitTags(String hashtags) {
        if (hashtags == null || hashtags.isBlank()) return Collections.emptyList();

        return Arrays.stream(hashtags.replace(",", "#").split("#"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}

