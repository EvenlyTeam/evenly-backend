package com.evenly.user.application.port.in;

import com.evenly.user.application.dto.AuthResult;
import com.evenly.user.application.dto.LoginCommand;

public interface LoginUseCase {

    AuthResult login(LoginCommand command);
}
