package com.evenly.group.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.evenly.common.domain.ConflictException;
import com.evenly.group.application.dto.CreateGroupCommand;
import com.evenly.group.application.dto.GroupDetail;
import com.evenly.group.application.port.out.SaveGroupPort;
import com.evenly.group.application.port.out.SaveParticipantPort;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateGroupServiceTest {

    @Mock
    SaveGroupPort saveGroupPort;

    @Mock
    SaveParticipantPort saveParticipantPort;

    @InjectMocks
    CreateGroupService service;

    @Test
    void 모임과_참여자를_함께_저장한다() {
        when(saveGroupPort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(saveParticipantPort.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        GroupDetail result =
                service.createGroup(new CreateGroupCommand("강릉 여행 모임", UUID.randomUUID(), List.of("준호", "민지")));

        assertThat(result.name()).isEqualTo("강릉 여행 모임");
        assertThat(result.participants()).extracting("name").containsExactly("준호", "민지");
        verify(saveParticipantPort).saveAll(anyList());
    }

    @Test
    void 참여자_없이도_생성된다() {
        when(saveGroupPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        GroupDetail result = service.createGroup(new CreateGroupCommand("빈 모임", UUID.randomUUID(), List.of()));

        assertThat(result.participants()).isEmpty();
        verify(saveParticipantPort, never()).saveAll(anyList());
    }

    @Test
    void 참여자_이름이_중복되면_충돌() {
        assertThatThrownBy(
                        () -> service.createGroup(new CreateGroupCommand("g", UUID.randomUUID(), List.of("준호", "준호"))))
                .isInstanceOf(ConflictException.class);
        verify(saveGroupPort, never()).save(any());
    }
}
