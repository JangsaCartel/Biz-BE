package com.jangsacartel.biz.ai.service;

import com.jangsacartel.biz.ai.dto.BizAiWeeklyResponseDTO;
import com.jangsacartel.biz.ai.enums.WeekPreset;

public interface AiAnalysisService {
    BizAiWeeklyResponseDTO getWeeklyAnalysis(WeekPreset weekPreset, int limit, int topK);
}

