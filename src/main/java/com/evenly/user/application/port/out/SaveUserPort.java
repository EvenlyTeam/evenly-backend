package com.evenly.user.application.port.out;

import com.evenly.user.domain.User;
import java.util.UUID;

public interface SaveUserPort {

    User save(User user);

    void deleteById(UUID id);
}
