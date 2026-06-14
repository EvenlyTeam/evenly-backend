package com.evenly.group.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.evenly.common.domain.NotFoundException;
import com.evenly.group.application.dto.SettlementInfo;
import com.evenly.group.application.dto.SharedSettlementInfo;
import com.evenly.group.application.port.in.GetSharedSettlementUseCase;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SharedController.class)
@AutoConfigureMockMvc(addFilters = false)
class SharedControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    GetSharedSettlementUseCase getSharedSettlementUseCase;

    @Test
    void 공유토큰으로_정산결과를_열람하면_200() throws Exception {
        given(getSharedSettlementUseCase.getSharedSettlement(any()))
                .willReturn(new SharedSettlementInfo(
                        UUID.randomUUID(), "강릉 여행 모임", new SettlementInfo(0, List.of(), List.of())));

        mvc.perform(get("/shared/tok123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupName").value("강릉 여행 모임"));
    }

    @Test
    void 잘못된_토큰이면_404() throws Exception {
        given(getSharedSettlementUseCase.getSharedSettlement(any()))
                .willThrow(new NotFoundException("SharedGroup", "bad"));

        mvc.perform(get("/shared/bad"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }
}
