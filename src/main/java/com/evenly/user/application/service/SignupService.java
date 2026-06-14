package com.evenly.user.application.service;

import com.evenly.common.domain.ConflictException;
import com.evenly.user.application.dto.AuthResult;
import com.evenly.user.application.dto.SignupCommand;
import com.evenly.user.application.port.in.SignupUseCase;
import com.evenly.user.application.port.out.LoadUserPort;
import com.evenly.user.application.port.out.PasswordHasher;
import com.evenly.user.application.port.out.SaveUserPort;
import com.evenly.user.application.port.out.TokenProvider;
import com.evenly.user.domain.Email;
import com.evenly.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class SignupService implements SignupUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final PasswordHasher passwordHasher;
    private final TokenProvider tokenProvider;

    SignupService(
            LoadUserPort loadUserPort,
            SaveUserPort saveUserPort,
            PasswordHasher passwordHasher,
            TokenProvider tokenProvider) {
        this.loadUserPort = loadUserPort;
        this.saveUserPort = saveUserPort;
        this.passwordHasher = passwordHasher;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AuthResult signup(SignupCommand command) {
        String email = new Email(command.email()).value();
        if (loadUserPort.existsByEmail(email)) {
            throw new ConflictException("이미 가입된 이메일입니다: " + email);
        }
        User saved = saveUserPort.save(User.register(email, passwordHasher.hash(command.rawPassword())));
        String token = tokenProvider.issueToken(saved.getId());
        return AuthResult.of(token, saved.getId(), saved.getEmail().value());
    }
}
