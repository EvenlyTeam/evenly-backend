package com.evenly.user.application.port.out;

import com.evenly.user.domain.User;

public interface SaveUserPort {

    User save(User user);
}
