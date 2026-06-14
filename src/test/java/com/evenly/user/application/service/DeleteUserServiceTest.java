package com.evenly.user.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.evenly.common.domain.NotFoundException;
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
class DeleteUserServiceTest {

    @Mock
    LoadUserPort loadUserPort;

    @Mock
    SaveUserPort saveUserPort;

    @InjectMocks
    DeleteUserService service;

    @Test
    void 회원을_삭제한다() {
        UUID id = UUID.randomUUID();
        when(loadUserPort.findById(id)).thenReturn(Optional.of(new User(id, "a@b.com", "준호", "h", null)));

        service.deleteUser(id);

        verify(saveUserPort).deleteById(id);
    }

    @Test
    void 없는_사용자면_NotFound() {
        UUID id = UUID.randomUUID();
        when(loadUserPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteUser(id)).isInstanceOf(NotFoundException.class);
        verify(saveUserPort, never()).deleteById(id);
    }
}
