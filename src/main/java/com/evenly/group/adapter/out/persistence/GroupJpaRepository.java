package com.evenly.group.adapter.out.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface GroupJpaRepository extends JpaRepository<GroupJpaEntity, UUID> {

    List<GroupJpaEntity> findByOwnerId(UUID ownerId);
}
