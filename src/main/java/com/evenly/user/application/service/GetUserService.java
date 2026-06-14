package com.evenly.user.application.service;

import com.evenly.common.domain.NotFoundException;
import com.evenly.user.application.dto.UserInfo;
import com.evenly.user.application.port.in.GetUserUseCase;
import com.evenly.user.application.port.out.LoadUserPort;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class GetUserService implements GetUserUseCase {

    private final LoadUserPort loadUserPort;

    GetUserService(LoadUserPort loadUserPort) {
        this.loadUserPort = loadUserPort;
    }

    @Override
    public UserInfo getUser(UUID userId) {
        return loadUserPort
                .findById(userId)
                .map(UserInfo::from)
                .orElseThrow(() -> new NotFoundException("User", userId));
    }
}
