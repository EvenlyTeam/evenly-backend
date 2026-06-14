package com.evenly.group.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.dto.GroupDetail;
import com.evenly.group.application.port.in.CreateGroupUseCase;
import com.evenly.group.application.port.in.GetGroupUseCase;
import com.evenly.group.application.port.in.ListGroupsUseCase;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GroupController.class)
@AutoConfigureMockMvc(addFilters = false)
class GroupControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    CreateGroupUseCase createGroupUseCase;

    @MockitoBean
    GetGroupUseCase getGroupUseCase;

    @MockitoBean
    ListGroupsUseCase listGroupsUseCase;

    @Test
    void 모임을_생성하면_201() throws Exception {
        UUID id = UUID.randomUUID();
        given(createGroupUseCase.createGroup(any()))
                .willReturn(new GroupDetail(id, "강릉 여행 모임", UUID.randomUUID(), null, OffsetDateTime.now(), List.of()));

        mvc.perform(post("/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"강릉 여행 모임\",\"participants\":[]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("강릉 여행 모임"));
    }

    @Test
    void 이름이_비면_400_VALIDATION_ERROR() throws Exception {
        mvc.perform(post("/groups").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void 본문_JSON이_깨지면_400() throws Exception {
        mvc.perform(post("/groups").contentType(MediaType.APPLICATION_JSON).content("{bad"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void 경로변수가_잘못된_UUID면_400() throws Exception {
        mvc.perform(get("/groups/not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void 없는_모임_조회는_404() throws Exception {
        UUID id = UUID.randomUUID();
        given(getGroupUseCase.getGroup(any())).willThrow(new NotFoundException("Group", id));

        mvc.perform(get("/groups/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void 목록_조회는_200() throws Exception {
        given(listGroupsUseCase.listGroups(any())).willReturn(List.of());

        mvc.perform(get("/groups")).andExpect(status().isOk());
    }
}
