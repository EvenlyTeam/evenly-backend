package com.evenly.settlement.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import org.junit.jupiter.api.Test;

class SettlementCalculatorTest {

    // ── 프로퍼티: 무작위 시나리오에서 항상 성립해야 하는 불변식 ──────────────

    @Property
    void 모든_순잔액의_합은_0이다(@ForAll("scenarios") Scenario s) {
        Settlement result = SettlementCalculator.calculate(s.participantIds(), s.lines());
        long sum = result.balances().stream().mapToLong(Balance::net).sum();
        assertThat(sum).isZero();
    }

    @Property
    void 모든_송금액은_양수다(@ForAll("scenarios") Scenario s) {
        Settlement result = SettlementCalculator.calculate(s.participantIds(), s.lines());
        assertThat(result.transfers()).allSatisfy(t -> assertThat(t.amount()).isPositive());
    }

    @Property
    void 송금_횟수는_0이_아닌_참여자수_미만이다(@ForAll("scenarios") Scenario s) {
        Settlement result = SettlementCalculator.calculate(s.participantIds(), s.lines());
        long nonZero = result.balances().stream().filter(b -> b.net() != 0).count();
        int upperBound = (int) Math.max(0, nonZero - 1);
        assertThat(result.transfers().size()).isLessThanOrEqualTo(upperBound);
    }

    @Property
    void 송금을_모두_적용하면_전원_0으로_정산된다(@ForAll("scenarios") Scenario s) {
        Settlement result = SettlementCalculator.calculate(s.participantIds(), s.lines());
        Map<UUID, Long> balance = new HashMap<>();
        result.balances().forEach(b -> balance.put(b.participantId(), b.net()));
        for (Transfer t : result.transfers()) {
            balance.merge(t.fromParticipantId(), t.amount(), Long::sum); // 줄 사람: 보내면 0에 가까워짐
            balance.merge(t.toParticipantId(), -t.amount(), Long::sum); // 받을 사람: 받으면 0에 가까워짐
        }
        assertThat(balance.values()).allSatisfy(v -> assertThat(v).isZero());
    }

    @Property
    void 총액은_모든_지출의_합이다(@ForAll("scenarios") Scenario s) {
        Settlement result = SettlementCalculator.calculate(s.participantIds(), s.lines());
        long expected = s.lines().stream().mapToLong(ExpenseLine::amount).sum();
        assertThat(result.total()).isEqualTo(expected);
    }

    // ── 구체 예시: 디자인(이미지4) — 120,000원 / 3명 분담 ──────────────────

    @Test
    void 디자인_예시_120000원_3명_분담() {
        UUID junho = UUID.randomUUID();
        UUID minji = UUID.randomUUID();
        UUID taewoo = UUID.randomUUID();
        UUID seoyeon = UUID.randomUUID();

        // 민지가 120,000 결제, 준호·민지·태우 3명 분담 (각 40,000)
        ExpenseLine line = new ExpenseLine(minji, 120_000, List.of(junho, minji, taewoo));
        Settlement result = SettlementCalculator.calculate(List.of(junho, minji, taewoo, seoyeon), List.of(line));

        assertThat(result.total()).isEqualTo(120_000);
        // 민지 +80,000 / 준호 -40,000 / 태우 -40,000 / 서연 0 → 모두 민지에게 송금, 2건
        assertThat(result.transfers()).hasSize(2);
        assertThat(result.transfers())
                .allSatisfy(t -> assertThat(t.toParticipantId()).isEqualTo(minji));
        long received = result.transfers().stream().mapToLong(Transfer::amount).sum();
        assertThat(received).isEqualTo(80_000);
    }

    @Test
    void 나머지_1원은_첫_분담자에게_배분된다() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        UUID c = UUID.randomUUID();

        // 100원을 3명이 분담 → 34/33/33, 첫 분담자(a) 가 1원 더
        ExpenseLine line = new ExpenseLine(a, 100, List.of(a, b, c));
        Settlement result = SettlementCalculator.calculate(List.of(a, b, c), List.of(line));

        Map<UUID, Long> net = new HashMap<>();
        result.balances().forEach(x -> net.put(x.participantId(), x.net()));
        // a: +100(결제) -34(분담) = +66, b: -33, c: -33
        assertThat(net.get(a)).isEqualTo(66);
        assertThat(net.get(b)).isEqualTo(-33);
        assertThat(net.get(c)).isEqualTo(-33);
    }

    // ── 무작위 시나리오 생성기 ──────────────────────────────────────────

    record Scenario(List<UUID> participantIds, List<ExpenseLine> lines) {}

    @Provide
    Arbitrary<Scenario> scenarios() {
        return Arbitraries.integers().between(2, 6).flatMap(n -> {
            List<UUID> ids =
                    IntStream.range(0, n).mapToObj(i -> UUID.randomUUID()).toList();
            return expenseLines(ids).list().ofMinSize(0).ofMaxSize(8).map(lines -> new Scenario(ids, lines));
        });
    }

    private Arbitrary<ExpenseLine> expenseLines(List<UUID> ids) {
        Arbitrary<UUID> payer = Arbitraries.of(ids);
        Arbitrary<Long> amount = Arbitraries.longs().between(1, 1_000_000);
        Arbitrary<List<UUID>> shares =
                Arbitraries.of(ids).list().ofMinSize(1).ofMaxSize(ids.size()).uniqueElements();
        return Combinators.combine(payer, amount, shares).as(ExpenseLine::new);
    }
}
