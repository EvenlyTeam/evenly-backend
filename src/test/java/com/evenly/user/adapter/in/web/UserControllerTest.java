package com.evenly.user.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.evenly.user.application.dto.UserInfo;
import com.evenly.user.application.port.in.DeleteUserUseCase;
import com.evenly.user.application.port.in.GetUserUseCase;
import com.evenly.user.application.port.in.UpdateUserUseCase;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    GetUserUseCase getUserUseCase;

    @MockitoBean
    UpdateUserUseCase updateUserUseCase;

    @MockitoBean
    DeleteUserUseCase deleteUserUseCase;

    @Test
    void 내_정보_조회는_200() throws Exception {
        given(getUserUseCase.getUser(any())).willReturn(new UserInfo(UUID.randomUUID(), "junho@example.com", "준호"));

        mvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("junho@example.com"))
                .andExpect(jsonPath("$.displayName").value("준호"));
    }

    @Test
    void 내_정보_수정은_200() throws Exception {
        given(updateUserUseCase.updateDisplayName(any(), any()))
                .willReturn(new UserInfo(UUID.randomUUID(), "junho@example.com", "준호2"));

        mvc.perform(patch("/users/me").contentType(MediaType.APPLICATION_JSON).content("{\"displayName\":\"준호2\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("준호2"));
    }

    @Test
    void 닉네임이_비면_400() throws Exception {
        mvc.perform(patch("/users/me").contentType(MediaType.APPLICATION_JSON).content("{\"displayName\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void 회원_탈퇴는_204() throws Exception {
        mvc.perform(delete("/users/me")).andExpect(status().isNoContent());
    }
}
