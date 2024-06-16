package com.sparta.fifteen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.fifteen.config.TestSecurityConfig;
import com.sparta.fifteen.dto.EmailVerifyRequestDto;
import com.sparta.fifteen.error.EmailAlreadyVerifiedException;
import com.sparta.fifteen.error.UserNotFoundException;
import com.sparta.fifteen.error.VerificationCodeExpiredException;
import com.sparta.fifteen.error.VerificationCodeMismatchException;
import com.sparta.fifteen.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;



    @MockBean
    private CommentService commentService;

    @MockBean
    private NewsFeedService newsFeedService;

    @MockBean
    private EmailVerificationService emailVerificationService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private LikeService likeService;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private EmailVerifyRequestDto requestDto;

    @BeforeEach
    void setup() {
        requestDto = new EmailVerifyRequestDto();
        requestDto.setUsername("testuser");
        requestDto.setVerificationCode("123456");
    }

    @Test
    void verifyEmail_Success() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string("이메일 인증이 완료되었습니다."));

        verify(emailVerificationService).verifyEmail("testuser", "123456");
    }

    @Test
    void verifyEmail_EmailAlreadyVerified() throws Exception {
        // given
        doThrow(new EmailAlreadyVerifiedException("이미 인증된 이메일입니다."))
                .when(emailVerificationService).verifyEmail(anyString(), anyString());

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string("이미 인증된 이메일입니다."));
    }

    @Test
    void verifyEmail_VerificationCodeMismatch() throws Exception {
        // given
        doThrow(new VerificationCodeMismatchException("인증 코드가 일치하지 않습니다."))
                .when(emailVerificationService).verifyEmail(anyString(), anyString());

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string("인증 코드가 일치하지 않습니다."));
    }

    @Test
    void verifyEmail_VerificationCodeExpired() throws Exception {
        // given
        doThrow(new VerificationCodeExpiredException("인증 코드가 만료되었습니다."))
                .when(emailVerificationService).verifyEmail(anyString(), anyString());

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string("인증 코드가 만료되었습니다."));
    }

    @Test
    void verifyEmail_UserNotFound() throws Exception {
        // given
        doThrow(new UserNotFoundException("사용자를 찾을 수 없습니다."))
                .when(emailVerificationService).verifyEmail(anyString(), anyString());

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then
        resultActions.andExpect(status().isNotFound())
                .andExpect(content().string("사용자를 찾을 수 없습니다."));
    }

}