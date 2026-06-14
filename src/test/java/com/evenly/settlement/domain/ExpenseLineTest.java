package com.evenly.settlement.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ExpenseLineTest {

    @Test
    void 균등분할이_딱_떨어지면_동일하게_나눈다() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        UUID c = UUID.randomUUID();

        Map<UUID, Long> owed = new ExpenseLine(a, 90000, List.of(a, b, c)).owedAmounts();

        assertThat(owed).containsValues(30000L, 30000L, 30000L);
        assertThat(owed.values().stream().mapToLong(Long::longValue).sum()).isEqualTo(90000);
    }

    @Test
    void 나머지_1원은_첫_분담자에게_배분된다() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        UUID c = UUID.randomUUID();

        Map<UUID, Long> owed = new ExpenseLine(a, 100, List.of(a, b, c)).owedAmounts();

        assertThat(owed.get(a)).isEqualTo(34); // 첫 분담자 +1
        assertThat(owed.get(b)).isEqualTo(33);
        assertThat(owed.get(c)).isEqualTo(33);
        assertThat(owed.values().stream().mapToLong(Long::longValue).sum()).isEqualTo(100);
    }

    @Test
    void 금액이_0이하거나_분담대상이_비면_거부한다() {
        UUID a = UUID.randomUUID();
        assertThatThrownBy(() -> new ExpenseLine(a, 0, List.of(a))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ExpenseLine(a, 100, List.of())).isInstanceOf(IllegalArgumentException.class);
    }
}
