package com.evenly.group.application.service;

import static org.mockito.Mockito.verify;

import com.evenly.group.application.port.out.SaveGroupPort;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteGroupServiceTest {

    @Mock
    SaveGroupPort saveGroupPort;

    @InjectMocks
    DeleteGroupService service;

    @Test
    void 모임을_삭제한다() {
        UUID id = UUID.randomUUID();

        service.deleteGroup(id);

        verify(saveGroupPort).deleteById(id);
    }
}
