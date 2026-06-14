package com.evenly.settlement.domain;

import java.util.List;

/** 정산 결과: 참여자별 순잔액, 최소 송금안, 총 지출액. */
public record Settlement(List<Balance> balances, List<Transfer> transfers, long total) {}
