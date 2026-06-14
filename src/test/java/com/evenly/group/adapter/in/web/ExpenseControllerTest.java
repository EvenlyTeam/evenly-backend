package com.evenly.group.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.evenly.group.application.GroupAccessGuard;
import com.evenly.group.application.dto.ExpenseInfo;
import com.evenly.group.application.port.in.AddExpenseUseCase;
import com.evenly.group.application.port.in.DeleteExpenseUseCase;
import com.evenly.group.application.port.in.ListExpensesUseCase;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ExpenseController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExpenseControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    AddExpenseUseCase addExpenseUseCase;

    @MockitoBean
    ListExpensesUseCase listExpensesUseCase;

    @MockitoBean
    DeleteExpenseUseCase deleteExpenseUseCase;

    @MockitoBean
    GroupAccessGuard groupAccessGuard;

    private final UUID groupId = UUID.randomUUID();

    @Test
    void 지출을_추가하면_201() throws Exception {
        UUID payer = UUID.randomUUID();
        given(addExpenseUseCase.addExpense(any()))
                .willReturn(new ExpenseInfo(UUID.randomUUID(), groupId, payer, "저녁", 120000, List.of(payer)));

        mvc.perform(post("/groups/" + groupId + "/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"payerId\":\"" + payer + "\",\"description\":\"저녁\",\"amount\":120000,"
                                + "\"shareParticipantIds\":[\"" + payer + "\"]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(120000));
    }

    @Test
    void 결제자가_참여자가_아니면_400() throws Exception {
        UUID payer = UUID.randomUUID();
        given(addExpenseUseCase.addExpense(any())).willThrow(new IllegalArgumentException("결제자가 모임 참여자가 아닙니다"));

        mvc.perform(post("/groups/" + groupId + "/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"payerId\":\"" + payer + "\",\"description\":\"저녁\",\"amount\":1000,"
                                + "\"shareParticipantIds\":[\"" + payer + "\"]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void 금액이_0이하면_400() throws Exception {
        UUID payer = UUID.randomUUID();

        mvc.perform(post("/groups/" + groupId + "/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"payerId\":\"" + payer + "\",\"description\":\"저녁\",\"amount\":0,"
                                + "\"shareParticipantIds\":[\"" + payer + "\"]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void 지출_목록은_200() throws Exception {
        given(listExpensesUseCase.listExpenses(any())).willReturn(List.of());

        mvc.perform(get("/groups/" + groupId + "/expenses")).andExpect(status().isOk());
    }

    @Test
    void 지출을_삭제하면_204() throws Exception {
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(
                        "/groups/" + groupId + "/expenses/" + UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }
}
