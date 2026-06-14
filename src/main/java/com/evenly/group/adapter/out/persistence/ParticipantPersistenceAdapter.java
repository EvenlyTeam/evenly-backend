package com.evenly.group.adapter.out.persistence;

import com.evenly.group.application.port.out.LoadParticipantPort;
import com.evenly.group.application.port.out.SaveParticipantPort;
import com.evenly.group.domain.Participant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
class ParticipantPersistenceAdapter implements SaveParticipantPort, LoadParticipantPort {

    private final ParticipantJpaRepository jpaRepository;

    ParticipantPersistenceAdapter(ParticipantJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Participant save(Participant participant) {
        return ParticipantMapper.toDomain(jpaRepository.save(ParticipantMapper.toEntity(participant)));
    }

    @Override
    public List<Participant> saveAll(List<Participant> participants) {
        return jpaRepository
                .saveAll(participants.stream().map(ParticipantMapper::toEntity).toList())
                .stream()
                .map(ParticipantMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Participant> findByGroupId(UUID groupId) {
        return jpaRepository.findByGroupId(groupId).stream()
                .map(ParticipantMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Participant> findById(UUID id) {
        return jpaRepository.findById(id).map(ParticipantMapper::toDomain);
    }

    @Override
    public boolean existsByGroupIdAndName(UUID groupId, String name) {
        return jpaRepository.existsByGroupIdAndName(groupId, name);
    }

    @Override
    public int countByGroupId(UUID groupId) {
        return (int) jpaRepository.countByGroupId(groupId);
    }
}
