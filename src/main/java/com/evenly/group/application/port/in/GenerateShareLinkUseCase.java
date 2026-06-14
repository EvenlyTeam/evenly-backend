package com.evenly.group.application.port.in;

import com.evenly.group.application.dto.ShareLinkInfo;
import java.util.UUID;

public interface GenerateShareLinkUseCase {

    ShareLinkInfo generateShareLink(UUID groupId);
}
