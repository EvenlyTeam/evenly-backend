package com.evenly.user.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.evenly.common.domain.ConflictException;
import com.evenly.common.domain.UnauthorizedException;
import com.evenly.user.application.dto.AuthResult;
import com.evenly.user.application.port.in.LoginUseCase;
import com.evenly.user.application.port.in.SignupUseCase;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    SignupUseCase signupUseCase;

    @MockitoBean
    LoginUseCase loginUseCase;

    @Test
    void 회원가입_201_및_토큰_반환() throws Exception {
        given(signupUseCase.signup(any()))
                .willReturn(AuthResult.of("jwt-token", UUID.randomUUID(), "junho@example.com"));

        mvc.perform(
                        post("/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"email\":\"junho@example.com\",\"displayName\":\"준호\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void 이메일_형식_틀리면_400() throws Exception {
        mvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"not-email\",\"displayName\":\"준호\",\"password\":\"password123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void 비밀번호가_짧으면_400() throws Exception {
        mvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"junho@example.com\",\"displayName\":\"준호\",\"password\":\"short\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void 이미_가입된_이메일이면_409() throws Exception {
        given(signupUseCase.signup(any())).willThrow(new ConflictException("이미 가입된 이메일"));

        mvc.perform(
                        post("/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"email\":\"junho@example.com\",\"displayName\":\"준호\",\"password\":\"password123\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void 로그인_200() throws Exception {
        given(loginUseCase.login(any())).willReturn(AuthResult.of("jwt-token", UUID.randomUUID(), "junho@example.com"));

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"junho@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"));
    }

    @Test
    void 자격증명_오류면_401() throws Exception {
        given(loginUseCase.login(any())).willThrow(new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다"));

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"junho@example.com\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }
}
