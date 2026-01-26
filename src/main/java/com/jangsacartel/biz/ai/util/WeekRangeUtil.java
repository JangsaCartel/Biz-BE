package com.jangsacartel.biz.ai.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

import com.jangsacartel.biz.ai.enums.WeekPreset;

public class WeekRangeUtil {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private WeekRangeUtil() {}

    public static LocalDate normalizeToMonday(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * preset 기준으로 [startMonday, endExclusiveMonday] 반환
     * - start: 월요일
     * - end: 다음 주 월요일(exclusive)
     */
    public static LocalDate[] resolveWeekRange(WeekPreset preset) {
        LocalDate today = LocalDate.now(KST);

        LocalDate thisWeekStart = normalizeToMonday(today);
        LocalDate thisWeekEndExclusive = thisWeekStart.plusWeeks(1);

        if (preset == WeekPreset.LAST) {
            LocalDate lastWeekStart = thisWeekStart.minusWeeks(1);
            LocalDate lastWeekEndExclusive = thisWeekStart;
            return new LocalDate[] { lastWeekStart, lastWeekEndExclusive };
        }

        // THIS
        return new LocalDate[] { thisWeekStart, thisWeekEndExclusive };
    }
}
