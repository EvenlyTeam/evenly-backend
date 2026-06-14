package com.evenly.user.application.port.out;

import com.evenly.user.domain.User;
import java.util.Optional;

public interface LoadUserPort {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
