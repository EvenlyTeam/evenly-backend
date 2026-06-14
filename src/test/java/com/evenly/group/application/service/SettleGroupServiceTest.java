package com.evenly.group.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.application.port.out.SaveGroupPort;
import com.evenly.group.domain.Group;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SettleGroupServiceTest {

    @Mock
    LoadGroupPort loadGroupPort;

    @Mock
    SaveGroupPort saveGroupPort;

    @InjectMocks
    SettleGroupService service;

    @Test
    void 정산완료로_표시한다() {
        UUID groupId = UUID.randomUUID();
        when(loadGroupPort.findById(groupId))
                .thenReturn(Optional.of(new Group(groupId, "g", UUID.randomUUID(), null, null)));

        service.settle(groupId);

        ArgumentCaptor<Group> captor = ArgumentCaptor.forClass(Group.class);
        verify(saveGroupPort).save(captor.capture());
        assertThat(captor.getValue().isSettled()).isTrue();
    }

    @Test
    void 정산완료를_해제한다() {
        UUID groupId = UUID.randomUUID();
        Group settled = new Group(groupId, "g", UUID.randomUUID(), null, null).markSettled();
        when(loadGroupPort.findById(groupId)).thenReturn(Optional.of(settled));

        service.reopen(groupId);

        ArgumentCaptor<Group> captor = ArgumentCaptor.forClass(Group.class);
        verify(saveGroupPort).save(captor.capture());
        assertThat(captor.getValue().isSettled()).isFalse();
    }

    @Test
    void 없는_모임이면_NotFound() {
        UUID groupId = UUID.randomUUID();
        when(loadGroupPort.findById(groupId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.settle(groupId)).isInstanceOf(NotFoundException.class);
        verify(saveGroupPort, never()).save(any());
    }
}
