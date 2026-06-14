package com.evenly.settlement.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 정산 계산의 입력 단위 — 지출 1건을 정산 도메인 관점으로 본 값.
 *
 * <p>{@code shareParticipantIds} 는 분담 대상이며, 균등분할 나머지(1~2원)는 앞 순서 분담자부터 배분된다.
 */
public record ExpenseLine(UUID payerId, long amount, List<UUID> shareParticipantIds) {

    public ExpenseLine {
        if (payerId == null) {
            throw new IllegalArgumentException("payerId must not be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (shareParticipantIds == null || shareParticipantIds.isEmpty()) {
            throw new IllegalArgumentException("shareParticipantIds must not be empty");
        }
        shareParticipantIds = List.copyOf(shareParticipantIds);
    }

    /**
     * 분담 대상별 부담액(원). 균등분할 후 1원 단위 나머지는 앞 순서 분담자부터 1원씩 배분한다.
     * (예: 100원 / 3명 → 첫 분담자 34, 나머지 33)
     */
    public Map<UUID, Long> owedAmounts() {
        int count = shareParticipantIds.size();
        long base = amount / count;
        long remainder = amount % count;
        Map<UUID, Long> owed = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) {
            owed.merge(shareParticipantIds.get(i), base + (i < remainder ? 1 : 0), Long::sum);
        }
        return owed;
    }
}
