package com.evenly.group.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface GroupJpaRepository extends JpaRepository<GroupJpaEntity, UUID> {

    List<GroupJpaEntity> findByOwnerId(UUID ownerId);

    Optional<GroupJpaEntity> findByShareToken(String shareToken);
}
