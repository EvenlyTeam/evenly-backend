package com.evenly.settlement.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;

/**
 * 균등 분할 정산기. 순잔액을 계산하고 송금 횟수가 최소가 되도록 송금안을 산출한다.
 *
 * <p>알고리즘(명세 6번):
 *
 * <ol>
 *   <li>각 참여자의 순잔액 = 낸 합 − 분담 합. 균등분할 나머지는 앞 순서 분담자부터 1원씩 배분.
 *   <li>순잔액 &gt; 0(받을 사람) / &lt; 0(줄 사람) 분리.
 *   <li>가장 많이 줄 사람 ↔ 가장 많이 받을 사람을 그리디 매칭.
 *   <li>매 송금마다 한쪽이 0이 되어 거래 수가 (0이 아닌 참여자 수 − 1) 이하로 수렴.
 * </ol>
 */
public final class SettlementCalculator {

    private SettlementCalculator() {}

    public static Settlement calculate(List<UUID> participantIds, List<ExpenseLine> lines) {
        Map<UUID, Long> net = new LinkedHashMap<>();
        for (UUID id : participantIds) {
            net.put(id, 0L);
        }

        long total = 0;
        for (ExpenseLine line : lines) {
            total += line.amount();
            net.merge(line.payerId(), line.amount(), Long::sum); // 결제자는 낸 만큼 +
            line.owedAmounts().forEach((participantId, owed) -> net.merge(participantId, -owed, Long::sum));
        }

        List<Balance> balances = net.entrySet().stream()
                .map(e -> new Balance(e.getKey(), e.getValue()))
                .toList();
        return new Settlement(balances, minimalTransfers(net), total);
    }

    private static List<Transfer> minimalTransfers(Map<UUID, Long> net) {
        // creditors: 받을 사람(net>0) 중 큰 순. debtors: 줄 사람(net<0) 중 가장 음수인 순.
        PriorityQueue<Map.Entry<UUID, Long>> creditors = new PriorityQueue<>(
                Comparator.comparingLong(Map.Entry<UUID, Long>::getValue).reversed());
        PriorityQueue<Map.Entry<UUID, Long>> debtors =
                new PriorityQueue<>(Comparator.comparingLong(Map.Entry::getValue));
        for (Map.Entry<UUID, Long> e : net.entrySet()) {
            if (e.getValue() > 0) {
                creditors.add(Map.entry(e.getKey(), e.getValue()));
            } else if (e.getValue() < 0) {
                debtors.add(Map.entry(e.getKey(), e.getValue()));
            }
        }

        List<Transfer> transfers = new ArrayList<>();
        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            Map.Entry<UUID, Long> creditor = creditors.poll();
            Map.Entry<UUID, Long> debtor = debtors.poll();
            long amount = Math.min(creditor.getValue(), -debtor.getValue());

            transfers.add(new Transfer(debtor.getKey(), creditor.getKey(), amount));

            long creditorLeft = creditor.getValue() - amount;
            long debtorLeft = debtor.getValue() + amount;
            if (creditorLeft > 0) {
                creditors.add(Map.entry(creditor.getKey(), creditorLeft));
            }
            if (debtorLeft < 0) {
                debtors.add(Map.entry(debtor.getKey(), debtorLeft));
            }
        }
        return transfers;
    }
}
