package com.sparta.fifteen.service;

import com.sparta.fifteen.dto.UserRequestDto;
import com.sparta.fifteen.entity.RefreshToken;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.entity.UserStatusEnum;
import com.sparta.fifteen.error.PasswordMismatchException;
import com.sparta.fifteen.error.UserWithdrawnException;
import com.sparta.fifteen.repository.UserRepository;
import com.sparta.fifteen.util.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class AuthenticationServiceTest {
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("정상적인 사용자 로그인")
    void loginUser_Success() {
        // given
        User user =  User
                .builder()
                .username("testuser1234")
                .name("lee")
                .password(passwordEncoder.encode("password1234"))
                .statusCode(UserStatusEnum.NORMAL.getStatus())
                .build();

        userRepository.save(user);

        UserRequestDto requestDto = UserRequestDto.builder()
                .username("testuser1234")
                .password("password1234")
                .build();

        // when
        String accessToken = authenticationService.loginUser(requestDto);

        // then
        assertNotNull(accessToken);
        assertTrue(JwtTokenProvider.validateToken(accessToken, "testuser1234"));
    }

    @Test
    @DisplayName("존재하지 않는 사용자 로그인 시도")
    void loginUser_UserNotFound() {
        // given
        UserRequestDto requestDto = UserRequestDto.builder()
                .username("nonexistent")
                .password("password123")
                .build();

        // when, then
        assertThrows(UsernameNotFoundException.class, () -> authenticationService.loginUser(requestDto));
    }

    @Test
    @DisplayName("탈퇴한 사용자 로그인 시도")
    void loginUser_UserWithdrawn() {
        // given
        User user = User
                .builder()
                .username("testuser1234")
                .name("lee")
                .password(passwordEncoder.encode("password1234"))
                .statusCode(UserStatusEnum.WITHDRAWN.getStatus())
                .build();

        userRepository.save(user);

        UserRequestDto requestDto = UserRequestDto.builder()
                .username("testuser1234")
                .password("password123")
                .build();

        // when, then
        assertThrows(UserWithdrawnException.class, () -> authenticationService.loginUser(requestDto));
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시도")
    void loginUser_PasswordMismatch() {
        // given
        User user = User
                .builder()
                .username("testuser1234")
                .name("lee")
                .password(passwordEncoder.encode("password1234"))
                .statusCode(UserStatusEnum.NORMAL.getStatus())
                .build();

        userRepository.save(user);

        UserRequestDto requestDto = UserRequestDto.builder()
                .username("testuser1234")
                .password("wrongpassword")
                .build();

        // when, then
        assertThrows(PasswordMismatchException.class, () -> authenticationService.loginUser(requestDto));
    }
}