package com.evenly.user.application.port.in;

import com.evenly.user.application.dto.UserInfo;
import java.util.UUID;

public interface UpdateUserUseCase {

    UserInfo updateDisplayName(UUID userId, String displayName);
}
