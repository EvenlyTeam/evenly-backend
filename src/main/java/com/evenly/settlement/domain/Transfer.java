package com.evenly.settlement.domain;

import java.util.UUID;

/** 정산 송금 1건: from → to 로 amount(원)를 보낸다. */
public record Transfer(UUID fromParticipantId, UUID toParticipantId, long amount) {}
