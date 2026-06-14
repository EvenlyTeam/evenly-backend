package com.evenly.user.application.port.out;

import com.evenly.user.domain.User;
import java.util.Optional;
import java.util.UUID;

public interface LoadUserPort {

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
