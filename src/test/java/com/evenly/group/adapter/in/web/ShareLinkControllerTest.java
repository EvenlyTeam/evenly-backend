package com.evenly.group.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.GroupAccessGuard;
import com.evenly.group.application.dto.ShareLinkInfo;
import com.evenly.group.application.port.in.GenerateShareLinkUseCase;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ShareLinkController.class)
@AutoConfigureMockMvc(addFilters = false)
class ShareLinkControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    GenerateShareLinkUseCase generateShareLinkUseCase;

    @MockitoBean
    GroupAccessGuard groupAccessGuard;

    @Test
    void 공유링크를_발급하면_200() throws Exception {
        given(generateShareLinkUseCase.generateShareLink(any())).willReturn(new ShareLinkInfo("tok123"));

        mvc.perform(post("/groups/" + UUID.randomUUID() + "/share-link"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shareToken").value("tok123"));
    }

    @Test
    void 없는_모임이면_404() throws Exception {
        UUID groupId = UUID.randomUUID();
        given(generateShareLinkUseCase.generateShareLink(any())).willThrow(new NotFoundException("Group", groupId));

        mvc.perform(post("/groups/" + groupId + "/share-link"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }
}
