package com.evenly.user.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.evenly.common.domain.ConflictException;
import com.evenly.common.domain.UnauthorizedException;
import com.evenly.user.application.dto.AuthTokens;
import com.evenly.user.application.port.in.LoginUseCase;
import com.evenly.user.application.port.in.RefreshTokenUseCase;
import com.evenly.user.application.port.in.SignupUseCase;
import jakarta.servlet.http.Cookie;
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

    @MockitoBean
    RefreshTokenUseCase refreshTokenUseCase;

    private AuthTokens tokens() {
        return new AuthTokens("access-tok", "refresh-tok", UUID.randomUUID(), "junho@example.com");
    }

    @Test
    void 회원가입_201_바디_및_access_refresh_쿠키() throws Exception {
        given(signupUseCase.signup(any())).willReturn(tokens());

        mvc.perform(
                        post("/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"email\":\"junho@example.com\",\"displayName\":\"준호\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access-tok"))
                .andExpect(cookie().value("accessToken", "access-tok"))
                .andExpect(cookie().httpOnly("accessToken", true))
                .andExpect(cookie().value("refreshToken", "refresh-tok"))
                .andExpect(cookie().httpOnly("refreshToken", true));
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
    void 로그인_200_바디_및_access_refresh_쿠키() throws Exception {
        given(loginUseCase.login(any())).willReturn(tokens());

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"junho@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-tok"))
                .andExpect(cookie().value("accessToken", "access-tok"))
                .andExpect(cookie().value("refreshToken", "refresh-tok"));
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

    @Test
    void 토큰_재발급_200_및_새_쿠키() throws Exception {
        given(refreshTokenUseCase.refresh(any())).willReturn(tokens());

        mvc.perform(post("/auth/refresh").cookie(new Cookie("refreshToken", "old-refresh")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-tok"))
                .andExpect(cookie().value("accessToken", "access-tok"))
                .andExpect(cookie().value("refreshToken", "refresh-tok"));
    }

    @Test
    void refresh_토큰_없거나_위조면_401() throws Exception {
        given(refreshTokenUseCase.refresh(any())).willThrow(new UnauthorizedException("유효하지 않은 refresh 토큰입니다"));

        mvc.perform(post("/auth/refresh"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void 로그아웃_204_및_쿠키_만료() throws Exception {
        mvc.perform(post("/auth/logout"))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge("accessToken", 0))
                .andExpect(cookie().maxAge("refreshToken", 0));
    }
}
