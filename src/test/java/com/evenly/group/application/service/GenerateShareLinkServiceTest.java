package com.evenly.group.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.dto.ShareLinkInfo;
import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.application.port.out.SaveGroupPort;
import com.evenly.group.domain.Group;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GenerateShareLinkServiceTest {

    @Mock
    LoadGroupPort loadGroupPort;

    @Mock
    SaveGroupPort saveGroupPort;

    @InjectMocks
    GenerateShareLinkService service;

    @Test
    void 토큰이_없으면_새로_발급한다() {
        UUID groupId = UUID.randomUUID();
        when(loadGroupPort.findById(groupId))
                .thenReturn(Optional.of(new Group(groupId, "강릉", UUID.randomUUID(), null, null)));
        when(saveGroupPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ShareLinkInfo result = service.generateShareLink(groupId);

        assertThat(result.shareToken()).isNotBlank();
        verify(saveGroupPort).save(any());
    }

    @Test
    void 이미_토큰이_있으면_기존_토큰을_재사용한다() {
        UUID groupId = UUID.randomUUID();
        when(loadGroupPort.findById(groupId))
                .thenReturn(Optional.of(new Group(groupId, "강릉", UUID.randomUUID(), "existing-token", null)));

        ShareLinkInfo result = service.generateShareLink(groupId);

        assertThat(result.shareToken()).isEqualTo("existing-token");
        verify(saveGroupPort, never()).save(any());
    }

    @Test
    void 없는_모임이면_NotFound() {
        UUID groupId = UUID.randomUUID();
        when(loadGroupPort.findById(groupId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.generateShareLink(groupId)).isInstanceOf(NotFoundException.class);
    }
}
