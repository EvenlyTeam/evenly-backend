package com.evenly.group.adapter.out.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface ParticipantJpaRepository extends JpaRepository<ParticipantJpaEntity, UUID> {

    List<ParticipantJpaEntity> findByGroupId(UUID groupId);

    boolean existsByGroupIdAndName(UUID groupId, String name);

    long countByGroupId(UUID groupId);
}
