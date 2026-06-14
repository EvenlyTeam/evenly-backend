package com.evenly.user.adapter.out.persistence;

import com.evenly.user.domain.User;

final class UserMapper {

    private UserMapper() {}

    static User toDomain(UserJpaEntity entity) {
        return new User(entity.getId(), entity.getEmail(), entity.getDisplayName(), entity.getPasswordHash(), null);
    }

    static UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(user.getId(), user.getEmail().value(), user.getDisplayName(), user.getPasswordHash());
    }
}
