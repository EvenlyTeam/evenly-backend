package com.evenly.group.adapter.out.persistence;

import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.application.port.out.SaveGroupPort;
import com.evenly.group.domain.Group;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
class GroupPersistenceAdapter implements LoadGroupPort, SaveGroupPort {

    private final GroupJpaRepository jpaRepository;

    GroupPersistenceAdapter(GroupJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Group> findById(UUID id) {
        return jpaRepository.findById(id).map(GroupMapper::toDomain);
    }

    @Override
    public Group save(Group group) {
        return GroupMapper.toDomain(jpaRepository.save(GroupMapper.toEntity(group)));
    }
}
