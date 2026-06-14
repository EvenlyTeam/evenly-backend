package com.evenly.settlement.domain;

import java.util.UUID;

/** 참여자의 순잔액 = (낸 금액 합) − (분담해야 할 금액 합). 양수면 받을 사람, 음수면 줄 사람. */
public record Balance(UUID participantId, long net) {}
