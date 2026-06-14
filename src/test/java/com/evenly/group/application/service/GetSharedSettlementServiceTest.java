package com.evenly.group.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.dto.SettlementInfo;
import com.evenly.group.application.dto.SharedSettlementInfo;
import com.evenly.group.application.port.in.GetSettlementUseCase;
import com.evenly.group.application.port.out.LoadGroupPort;
import com.evenly.group.domain.Group;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetSharedSettlementServiceTest {

    @Mock
    LoadGroupPort loadGroupPort;

    @Mock
    GetSettlementUseCase getSettlementUseCase;

    @InjectMocks
    GetSharedSettlementService service;

    @Test
    void 토큰으로_모임_이름과_정산결과를_반환한다() {
        UUID groupId = UUID.randomUUID();
        when(loadGroupPort.findByShareToken("tok"))
                .thenReturn(Optional.of(new Group(groupId, "강릉 여행 모임", UUID.randomUUID(), "tok", null)));
        when(getSettlementUseCase.getSettlement(groupId)).thenReturn(new SettlementInfo(0, List.of(), List.of()));

        SharedSettlementInfo result = service.getSharedSettlement("tok");

        assertThat(result.groupName()).isEqualTo("강릉 여행 모임");
        assertThat(result.groupId()).isEqualTo(groupId);
        assertThat(result.settlement()).isNotNull();
    }

    @Test
    void 유효하지_않은_토큰이면_NotFound() {
        when(loadGroupPort.findByShareToken("bad")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getSharedSettlement("bad")).isInstanceOf(NotFoundException.class);
    }
}
