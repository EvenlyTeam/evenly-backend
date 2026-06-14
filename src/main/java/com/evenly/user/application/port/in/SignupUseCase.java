package com.evenly.user.application.port.in;

import com.evenly.user.application.dto.AuthTokens;
import com.evenly.user.application.dto.SignupCommand;

public interface SignupUseCase {

    AuthTokens signup(SignupCommand command);
}
