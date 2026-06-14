package com.evenly.group.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.evenly.common.domain.ForbiddenException;
import com.evenly.group.application.GroupAccessGuard;
import com.evenly.group.application.dto.SettlementInfo;
import com.evenly.group.application.port.in.GetSettlementUseCase;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SettlementController.class)
@AutoConfigureMockMvc(addFilters = false)
class SettlementControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    GetSettlementUseCase getSettlementUseCase;

    @MockitoBean
    GroupAccessGuard groupAccessGuard;

    @Test
    void 정산_결과_조회는_200() throws Exception {
        given(getSettlementUseCase.getSettlement(any())).willReturn(new SettlementInfo(0, List.of(), List.of()));

        mvc.perform(get("/groups/" + UUID.randomUUID() + "/settlement")).andExpect(status().isOk());
    }

    @Test
    void 소유자가_아니면_403() throws Exception {
        given(groupAccessGuard.requireOwner(any(), any())).willThrow(new ForbiddenException("권한 없음"));

        mvc.perform(get("/groups/" + UUID.randomUUID() + "/settlement"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }
}
