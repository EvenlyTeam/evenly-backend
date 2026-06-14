package com.evenly.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.evenly.common.domain.NotFoundException;
import com.evenly.user.application.dto.UserInfo;
import com.evenly.user.application.port.out.LoadUserPort;
import com.evenly.user.application.port.out.SaveUserPort;
import com.evenly.user.domain.User;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateUserServiceTest {

    @Mock
    LoadUserPort loadUserPort;

    @Mock
    SaveUserPort saveUserPort;

    @InjectMocks
    UpdateUserService service;

    @Test
    void 닉네임을_변경한다() {
        UUID id = UUID.randomUUID();
        when(loadUserPort.findById(id)).thenReturn(Optional.of(new User(id, "junho@example.com", "준호", "h", null)));
        when(saveUserPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserInfo result = service.updateDisplayName(id, "준호2");

        assertThat(result.displayName()).isEqualTo("준호2");
        assertThat(result.email()).isEqualTo("junho@example.com");
    }

    @Test
    void 없는_사용자면_NotFound() {
        UUID id = UUID.randomUUID();
        when(loadUserPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateDisplayName(id, "x")).isInstanceOf(NotFoundException.class);
    }
}
