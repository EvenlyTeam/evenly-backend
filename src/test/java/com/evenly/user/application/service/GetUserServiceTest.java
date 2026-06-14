package com.evenly.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.evenly.common.domain.NotFoundException;
import com.evenly.user.application.dto.UserInfo;
import com.evenly.user.application.port.out.LoadUserPort;
import com.evenly.user.domain.User;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetUserServiceTest {

    @Mock
    LoadUserPort loadUserPort;

    @InjectMocks
    GetUserService service;

    @Test
    void 내_정보를_반환한다() {
        UUID id = UUID.randomUUID();
        when(loadUserPort.findById(id)).thenReturn(Optional.of(new User(id, "junho@example.com", "준호", "h", null)));

        UserInfo result = service.getUser(id);

        assertThat(result.email()).isEqualTo("junho@example.com");
        assertThat(result.displayName()).isEqualTo("준호");
    }

    @Test
    void 없는_사용자면_NotFound() {
        UUID id = UUID.randomUUID();
        when(loadUserPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUser(id)).isInstanceOf(NotFoundException.class);
    }
}
