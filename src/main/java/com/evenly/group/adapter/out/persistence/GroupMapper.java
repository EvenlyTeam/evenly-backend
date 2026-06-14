package com.evenly.group.adapter.out.persistence;

import com.evenly.group.domain.Group;

final class GroupMapper {

    private GroupMapper() {}

    static Group toDomain(GroupJpaEntity entity) {
        return new Group(
                entity.getId(),
                entity.getName(),
                entity.getOwnerId(),
                entity.getShareToken(),
                entity.getCreatedAt(),
                entity.getSettledAt());
    }

    static GroupJpaEntity toEntity(Group group) {
        // createdAt 은 JPA Auditing(BaseJpaEntity)이 영속 시점에 채운다.
        return new GroupJpaEntity(
                group.getId(),
                group.getName().value(),
                group.getOwnerId(),
                group.getShareToken(),
                group.getSettledAt());
    }
}
