package com.jangsacartel.biz.ai.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

public class WeekLabelUtil {

    private WeekLabelUtil() {}

    /**
     * 라벨 규칙
     * - 주 범위는 월요일 시작(weekStartMonday)
     * - 라벨의 "월"은 목요일(anchor=weekStartMonday+3)이 속한 달로 결정
     * - "달의 1주차"는 그 달의 1일이 포함된 주
     *
     * 예: "2025년 12월 2주차"
     */
    public static String buildLabel(LocalDate weekStartMonday) {
        LocalDate anchor = weekStartMonday.plusDays(3); // Thu

        int year = anchor.getYear();
        int month = anchor.getMonthValue();

        LocalDate firstOfMonth = LocalDate.of(year, month, 1);

        // 그 달 1일이 포함된 주의 월요일이 1주차 시작점
        LocalDate firstWeekStartMonday =
            firstOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        long daysDiff = ChronoUnit.DAYS.between(firstWeekStartMonday, weekStartMonday);
        int weekOfMonth = (int) (daysDiff / 7) + 1;

        return String.format("%d년 %d월 %d주차", year, month, weekOfMonth);
    }
}
