package com.evenly.group.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ExpenseTest {

    private final UUID groupId = UUID.randomUUID();
    private final UUID payer = UUID.randomUUID();

    @Test
    void 유효한_지출을_생성한다() {
        Expense expense = new Expense(UUID.randomUUID(), groupId, payer, "저녁", 120000, List.of(payer));
        assertThat(expense.getAmount()).isEqualTo(120000);
        assertThat(expense.getShareParticipantIds()).containsExactly(payer);
    }

    @Test
    void 분담대상_리스트는_불변이다() {
        Expense expense = new Expense(UUID.randomUUID(), groupId, payer, "저녁", 100, List.of(payer));
        assertThatThrownBy(() -> expense.getShareParticipantIds().add(UUID.randomUUID()))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void 금액이_0이하면_거부한다() {
        assertThatThrownBy(() -> new Expense(UUID.randomUUID(), groupId, payer, "x", 0, List.of(payer)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 분담대상이_비면_거부한다() {
        assertThatThrownBy(() -> new Expense(UUID.randomUUID(), groupId, payer, "x", 100, List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 내용이_비면_거부한다() {
        assertThatThrownBy(() -> new Expense(UUID.randomUUID(), groupId, payer, " ", 100, List.of(payer)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 결제자가_null이면_거부한다() {
        assertThatThrownBy(() -> new Expense(UUID.randomUUID(), groupId, null, "x", 100, List.of(payer)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_는_id를_발급한다() {
        Expense expense = Expense.create(groupId, payer, "저녁", 120000, List.of(payer));

        assertThat(expense.getId()).isNotNull();
        assertThat(expense.getGroupId()).isEqualTo(groupId);
        assertThat(expense.getDescription()).isEqualTo("저녁");
    }

    @Test
    void belongsTo_는_같은_모임이면_참() {
        Expense expense = Expense.create(groupId, payer, "저녁", 100, List.of(payer));

        assertThat(expense.belongsTo(groupId)).isTrue();
        assertThat(expense.belongsTo(UUID.randomUUID())).isFalse();
    }

    @Test
    void update_는_id와_모임은_유지하고_나머지를_교체한다() {
        Expense expense = Expense.create(groupId, payer, "저녁", 100, List.of(payer));
        UUID newPayer = UUID.randomUUID();

        Expense updated = expense.update(newPayer, "택시", 30000, List.of(payer, newPayer));

        assertThat(updated.getId()).isEqualTo(expense.getId());
        assertThat(updated.getGroupId()).isEqualTo(groupId);
        assertThat(updated.getPayerId()).isEqualTo(newPayer);
        assertThat(updated.getAmount()).isEqualTo(30000);
        assertThat(updated.getDescription()).isEqualTo("택시");
    }
}
