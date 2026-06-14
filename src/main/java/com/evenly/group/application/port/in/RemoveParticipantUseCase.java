package com.evenly.group.application.port.in;

import java.util.UUID;

public interface RemoveParticipantUseCase {

    void removeParticipant(UUID groupId, UUID participantId);
}
