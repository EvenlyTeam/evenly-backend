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
import com.evenly.user.application.port.out.LoadUserPort;
import com.evenly.user.domain.User;
import java.util.List;
import java.util.Optional;
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

    @Mock
    LoadUserPort loadUserPort;

    @InjectMocks
    CreateGroupService service;

    private final UUID owner = UUID.randomUUID();

    private void stubCreatorAndSaves() {
        when(loadUserPort.findById(owner)).thenReturn(Optional.of(new User(owner, "owner@e.com", "오너", "h", null)));
        when(saveGroupPort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(saveParticipantPort.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void 생성자를_연결된_참여자로_자동_포함한다() {
        stubCreatorAndSaves();

        GroupDetail result = service.createGroup(new CreateGroupCommand("강릉 여행 모임", owner, List.of("준호", "민지")));

        // 생성자(오너)가 앞에 자동 포함 + 요청 이름들
        assertThat(result.participants()).extracting("name").containsExactly("오너", "준호", "민지");
        assertThat(result.participants().get(0).userId()).isEqualTo(owner);
    }

    @Test
    void 참여자_이름에_생성자_닉네임이_있으면_그_참여자를_연결한다() {
        stubCreatorAndSaves();

        GroupDetail result = service.createGroup(new CreateGroupCommand("g", owner, List.of("오너", "민지")));

        assertThat(result.participants()).extracting("name").containsExactly("오너", "민지");
        // "오너" 참여자가 생성자와 연결됨
        assertThat(result.participants().get(0).userId()).isEqualTo(owner);
        assertThat(result.participants().get(1).userId()).isNull();
    }

    @Test
    void 참여자를_안_넣어도_생성자는_포함된다() {
        stubCreatorAndSaves();

        GroupDetail result = service.createGroup(new CreateGroupCommand("빈 모임", owner, List.of()));

        assertThat(result.participants()).extracting("name").containsExactly("오너");
    }

    @Test
    void 참여자_이름이_중복되면_충돌() {
        assertThatThrownBy(() -> service.createGroup(new CreateGroupCommand("g", owner, List.of("준호", "준호"))))
                .isInstanceOf(ConflictException.class);
        verify(saveGroupPort, never()).save(any());
    }
}
