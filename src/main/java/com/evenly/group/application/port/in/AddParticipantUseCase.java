package com.evenly.group.application.port.in;

import com.evenly.group.application.dto.AddParticipantCommand;
import com.evenly.group.application.dto.ParticipantInfo;

public interface AddParticipantUseCase {

    ParticipantInfo addParticipant(AddParticipantCommand command);
}
