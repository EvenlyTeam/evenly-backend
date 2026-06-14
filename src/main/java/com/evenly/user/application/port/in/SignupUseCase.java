package com.evenly.user.application.port.in;

import com.evenly.user.application.dto.AuthResult;
import com.evenly.user.application.dto.SignupCommand;

public interface SignupUseCase {

    AuthResult signup(SignupCommand command);
}
