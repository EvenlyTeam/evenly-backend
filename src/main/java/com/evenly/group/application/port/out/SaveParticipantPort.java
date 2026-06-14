package com.evenly.group.application.port.out;

import com.evenly.group.domain.Participant;
import java.util.List;
import java.util.UUID;

public interface SaveParticipantPort {

    Participant save(Participant participant);

    List<Participant> saveAll(List<Participant> participants);

    void deleteById(UUID id);
}
