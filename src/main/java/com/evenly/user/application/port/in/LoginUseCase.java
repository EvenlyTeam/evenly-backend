package com.evenly.user.application.port.in;

import com.evenly.user.application.dto.AuthTokens;
import com.evenly.user.application.dto.LoginCommand;

public interface LoginUseCase {

    AuthTokens login(LoginCommand command);
}
