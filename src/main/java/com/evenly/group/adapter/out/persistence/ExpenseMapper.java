package com.evenly.group.adapter.out.persistence;

import com.evenly.group.domain.Expense;

final class ExpenseMapper {

    private ExpenseMapper() {}

    static Expense toDomain(ExpenseJpaEntity entity) {
        return new Expense(
                entity.getId(),
                entity.getGroupId(),
                entity.getPayerId(),
                entity.getDescription(),
                entity.getAmount(),
                entity.getShareParticipantIds());
    }

    static ExpenseJpaEntity toEntity(Expense expense) {
        return new ExpenseJpaEntity(
                expense.getId(),
                expense.getGroupId(),
                expense.getPayerId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getShareParticipantIds());
    }
}
