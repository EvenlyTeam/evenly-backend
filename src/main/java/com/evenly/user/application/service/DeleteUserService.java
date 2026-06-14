package com.evenly.user.application.service;

import com.evenly.common.domain.NotFoundException;
import com.evenly.user.application.port.in.DeleteUserUseCase;
import com.evenly.user.application.port.out.LoadUserPort;
import com.evenly.user.application.port.out.SaveUserPort;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class DeleteUserService implements DeleteUserUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;

    DeleteUserService(LoadUserPort loadUserPort, SaveUserPort saveUserPort) {
        this.loadUserPort = loadUserPort;
        this.saveUserPort = saveUserPort;
    }

    @Override
    public void deleteUser(UUID userId) {
        if (loadUserPort.findById(userId).isEmpty()) {
            throw new NotFoundException("User", userId);
        }
        saveUserPort.deleteById(userId);
    }
}
