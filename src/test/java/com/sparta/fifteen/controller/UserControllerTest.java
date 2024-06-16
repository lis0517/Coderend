package com.sparta.fifteen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.fifteen.config.JwtConfig;
import com.sparta.fifteen.config.TestSecurityConfig;
import com.sparta.fifteen.dto.CommentRequestDto;
import com.sparta.fifteen.dto.UserRegisterRequestDto;
import com.sparta.fifteen.dto.UserRegisterResponseDto;
import com.sparta.fifteen.dto.UserRequestDto;
import com.sparta.fifteen.entity.NewsFeed;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.entity.UserRoleEnum;
import com.sparta.fifteen.error.PasswordMismatchException;
import com.sparta.fifteen.error.UserPendingException;
import com.sparta.fifteen.error.UserWithdrawnException;
import com.sparta.fifteen.security.UserDetailsImpl;
import com.sparta.fifteen.service.*;
import com.sparta.fifteen.util.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Date;
import java.util.InputMismatchException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class UserControllerTest {

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

    private UserRegisterRequestDto userRegisterRequestDto;
    private UserRegisterResponseDto userRegisterResponseDto;
    private UserRequestDto userRequestDto;

    private User user;

    private String token;

    @BeforeEach
    void setup() {

        user = User .builder()
                .username("testuser1234")
                .password("password1234")
                .name("Test User")
                .oneLine("hi")
                .email("test@example.com")
                .build();

        userRegisterRequestDto = UserRegisterRequestDto.builder()
                .username("testuser1234")
                .password("password1234!")
                .name("John Doe")
                .email("test@example.com")
                .oneLine("Test user")
                .build();

        userRegisterResponseDto = UserRegisterResponseDto.builder()
                .id(1L)
                .username("testuser1234")
                .name("John Doe")
                .email("test@example.com")
                .oneLine("Test user")
                .statusCode("ACTIVE")
                .createdOn(new Timestamp(System.currentTimeMillis()))
                .build();

        userRequestDto = UserRequestDto.builder()
                .username("testuser1234")
                .password("password1234!")
                .build();

        // JwtConfig 값을 직접 설정
        String secretKey = "7YWM7Iqk7Yq47YKk7J6F64uI64ukLg==";
        long accessTokenExpiration = 3600000; // 1시간

        // JWT 토큰 생성

        token = Jwts.builder()
                .claim("auth", UserRoleEnum.USER)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    @Test
    void signup_Success() throws Exception {
        // given
        given(userService.registerUser(any(UserRegisterRequestDto.class))).willReturn(userRegisterResponseDto);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterRequestDto)));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userRegisterResponseDto.getId()))
                .andExpect(jsonPath("$.username").value(userRegisterResponseDto.getUsername()))
                .andExpect(jsonPath("$.name").value(userRegisterResponseDto.getName()))
                .andExpect(jsonPath("$.email").value(userRegisterResponseDto.getEmail()))
                .andExpect(jsonPath("$.oneLine").value(userRegisterResponseDto.getOneLine()))
                .andExpect(jsonPath("$.statusCode").value(userRegisterResponseDto.getStatusCode()));
    }

    @Test
    void signup_DuplicateUser() throws Exception {
        // given
        given(userService.registerUser(any(UserRegisterRequestDto.class))).willThrow(new IllegalArgumentException());

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterRequestDto)));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string("이미 존재하는 User ID 입니다. 회원가입에 실패하셨습니다."));
    }

    @Test
    void userLogin_Success() throws Exception {
        // 내부 로직 때문인지 계속 500 오류 발생..

        // given
        given(authenticationService.loginUser(any(UserRequestDto.class))).willReturn(token);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(header().string(JwtConfig.staticHeader, JwtConfig.staticTokenPrefix + token))
                .andExpect(content().string(token));
    }

    @Test
    void userLogin_UserNotFound() throws Exception {
        // given
        given(authenticationService.loginUser(any(UserRequestDto.class))).willThrow(new UsernameNotFoundException("User not found"));

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)));

        // then
        resultActions.andExpect(status().isNotFound())
                .andExpect(content().string("존재하지 않는 사용자입니다."));
    }

    @Test
    void userLogin_UserWithdrawn() throws Exception {
        // given
        given(authenticationService.loginUser(any(UserRequestDto.class))).willThrow(new UserWithdrawnException("User withdrawn"));

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)));

        // then
        resultActions.andExpect(status().isForbidden())
                .andExpect(content().string("탈퇴한 계정입니다."));
    }

    @Test
    void userLogin_UserPending() throws Exception {
        // given
        given(authenticationService.loginUser(any(UserRequestDto.class))).willThrow(new UserPendingException("User pending"));

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)));

        // then
        resultActions.andExpect(status().isUnauthorized())
                .andExpect(content().string("인증 대기 상태입니다. 이메일 인증을 해주세요."));
    }

    @Test
    void userLogin_PasswordMismatch() throws Exception {
        // given
        given(authenticationService.loginUser(any(UserRequestDto.class))).willThrow(new PasswordMismatchException("Password mismatch"));

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string("패스워드가 일치하지 않습니다."));
    }

    @Test
    @WithMockUser(username = "testuser1234")
    void userLogout_Success() throws Exception {
        // 내부 로직 때문인지 계속 500 오류 발생..

        // given
        doNothing().when(authenticationService).logoutUser(token, "testuser1234");

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/user/logout")
                .header("Authorization", "Bearer " + token));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string("로그아웃되었습니다."));
    }

    @Test
    @WithMockUser
    void userWithdraw_Success() throws Exception {
        // given
        UserRequestDto requestDto = UserRequestDto.builder()
                .username("testuser1234")
                .password("password1234!")
                .build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        //when
        doNothing().when(userService).withdrawUser(user.getUsername(), requestDto.getPassword());

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/user")
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string("회원 탈퇴가 완료되었습니다."));
    }

    @Test
    @WithMockUser
    void userWithdraw_DifferentUser() throws Exception {
        // given
        UserRequestDto requestDto = UserRequestDto.builder()
                .username("differentuser")
                .password("password1234!")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string("현재 로그인한 사용자가 아닙니다."));
    }
}