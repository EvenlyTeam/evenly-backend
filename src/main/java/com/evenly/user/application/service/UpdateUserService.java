package com.evenly.user.application.service;

import com.evenly.common.domain.NotFoundException;
import com.evenly.user.application.dto.UserInfo;
import com.evenly.user.application.port.in.UpdateUserUseCase;
import com.evenly.user.application.port.out.LoadUserPort;
import com.evenly.user.application.port.out.SaveUserPort;
import com.evenly.user.domain.User;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class UpdateUserService implements UpdateUserUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;

    UpdateUserService(LoadUserPort loadUserPort, SaveUserPort saveUserPort) {
        this.loadUserPort = loadUserPort;
        this.saveUserPort = saveUserPort;
    }

    @Override
    public UserInfo updateDisplayName(UUID userId, String displayName) {
        User user = loadUserPort.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));
        return UserInfo.from(saveUserPort.save(user.withDisplayName(displayName)));
    }
}
