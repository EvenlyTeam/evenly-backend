package com.evenly.group.adapter.out.persistence;

import com.evenly.group.application.port.out.LoadExpensePort;
import com.evenly.group.application.port.out.SaveExpensePort;
import com.evenly.group.domain.Expense;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
class ExpensePersistenceAdapter implements SaveExpensePort, LoadExpensePort {

    private final ExpenseJpaRepository jpaRepository;

    ExpensePersistenceAdapter(ExpenseJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Expense save(Expense expense) {
        return ExpenseMapper.toDomain(jpaRepository.save(ExpenseMapper.toEntity(expense)));
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Expense> findByGroupId(UUID groupId) {
        return jpaRepository.findByGroupId(groupId).stream()
                .map(ExpenseMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Expense> findById(UUID id) {
        return jpaRepository.findById(id).map(ExpenseMapper::toDomain);
    }
}
