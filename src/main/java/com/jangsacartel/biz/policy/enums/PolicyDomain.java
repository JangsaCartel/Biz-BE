package com.jangsacartel.biz.policy.enums;

import java.util.Arrays;

public enum PolicyDomain {
    ALL(null, "전체 분야"),
    FINANCE("01", "금융 분야"),
    TECH("02", "기술 분야"),
    HR("03", "인력 분야"),
    EXPORT("04", "수출 분야"),
    DOMESTIC("05", "내수 분야"),
    STARTUP("06", "창업 분야"),
    MANAGEMENT("07", "경영 분야"),
    ETC("09", "기타 분야");

    private final String code;
    private final String label;

    PolicyDomain(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() { return code; }

    public static PolicyDomain fromLabel(String label) {
        if (label == null || label.isEmpty()) return ALL;
        return Arrays.stream(values())
                     .filter(d -> d.label.equals(label))
                     .findFirst()
                     .orElse(ALL);
    }
}

