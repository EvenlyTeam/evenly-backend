package com.evenly.group.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.evenly.common.domain.ConflictException;
import com.evenly.group.application.dto.ParticipantInfo;
import com.evenly.group.application.port.in.AddParticipantUseCase;
import com.evenly.group.application.port.in.RemoveParticipantUseCase;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ParticipantController.class)
@AutoConfigureMockMvc(addFilters = false)
class ParticipantControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    AddParticipantUseCase addParticipantUseCase;

    @MockitoBean
    RemoveParticipantUseCase removeParticipantUseCase;

    private final UUID groupId = UUID.randomUUID();

    @Test
    void 참여자를_추가하면_201() throws Exception {
        given(addParticipantUseCase.addParticipant(any()))
                .willReturn(new ParticipantInfo(UUID.randomUUID(), "지호", null));

        mvc.perform(post("/groups/" + groupId + "/participants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"지호\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("지호"));
    }

    @Test
    void 이름이_중복이면_409() throws Exception {
        given(addParticipantUseCase.addParticipant(any())).willThrow(new ConflictException("중복"));

        mvc.perform(post("/groups/" + groupId + "/participants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"준호\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void 이름이_비면_400() throws Exception {
        mvc.perform(post("/groups/" + groupId + "/participants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void 참여자를_삭제하면_204() throws Exception {
        UUID participantId = UUID.randomUUID();

        mvc.perform(delete("/groups/" + groupId + "/participants/" + participantId))
                .andExpect(status().isNoContent());
    }

    @Test
    void 삭제_대상이_없으면_404() throws Exception {
        UUID participantId = UUID.randomUUID();
        doThrow(new com.evenly.common.domain.NotFoundException("Participant", participantId))
                .when(removeParticipantUseCase)
                .removeParticipant(any(), any());

        mvc.perform(delete("/groups/" + groupId + "/participants/" + participantId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }
}
