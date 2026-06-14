package com.evenly.group.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.evenly.common.domain.ForbiddenException;
import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.domain.Group;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupAccessGuardTest {

    @Mock
    LoadGroupPort loadGroupPort;

    @InjectMocks
    GroupAccessGuard guard;

    @Test
    void 소유자면_모임을_반환한다() {
        UUID owner = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        when(loadGroupPort.findById(groupId)).thenReturn(Optional.of(new Group(groupId, "g", owner, null, null)));

        Group result = guard.requireOwner(groupId, owner);

        assertThat(result.getId()).isEqualTo(groupId);
    }

    @Test
    void 없는_모임이면_NotFound() {
        UUID groupId = UUID.randomUUID();
        when(loadGroupPort.findById(groupId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> guard.requireOwner(groupId, UUID.randomUUID())).isInstanceOf(NotFoundException.class);
    }

    @Test
    void 소유자가_아니면_Forbidden() {
        UUID groupId = UUID.randomUUID();
        when(loadGroupPort.findById(groupId))
                .thenReturn(Optional.of(new Group(groupId, "g", UUID.randomUUID(), null, null)));

        assertThatThrownBy(() -> guard.requireOwner(groupId, UUID.randomUUID())).isInstanceOf(ForbiddenException.class);
    }
}
