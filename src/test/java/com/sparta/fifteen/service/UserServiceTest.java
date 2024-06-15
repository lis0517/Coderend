package com.sparta.fifteen.service;

import com.sparta.fifteen.dto.UserRegisterRequestDto;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.entity.UserStatusEnum;
import com.sparta.fifteen.error.PasswordMismatchException;
import com.sparta.fifteen.error.UserAlreadyExistsException;
import com.sparta.fifteen.error.UserWithdrawnException;
import com.sparta.fifteen.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.InputMismatchException;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class UserServiceTest {


    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("정상적인 사용자 등록")
    void registerUser_Success() {
        // given
        UserRegisterRequestDto requestDto = UserRegisterRequestDto.builder()
                .username("testuser1234")
                .password("password123")
                .email("email@example.com")
                .build();

        // when
        userService.registerUser(requestDto);

        // then
        User foundUser = userRepository.findByUsername("testuser1234").orElse(null);
        assertNotNull(foundUser);
        assertEquals(UserStatusEnum.PENDING.getStatus(), foundUser.getStatusCode());
        assertTrue(passwordEncoder.matches("password123", foundUser.getPassword()));
    }

    @Test
    @DisplayName("중복 사용자명 등록 시도")
    void registerUser_UserAlreadyExists() {
        // given
        UserRegisterRequestDto requestDto1 = UserRegisterRequestDto.builder()
                .username("testuser1234")
                .password("password123")
                .email("email1@example.com")
                .build();
        UserRegisterRequestDto requestDto2 = UserRegisterRequestDto.builder()
                .username("testuser1234")
                .password("password456")
                .email("email2@example.com")
                .build();
        userService.registerUser(requestDto1);

        // when, then
        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(requestDto2));
    }

    @Test
    @DisplayName("짧은 비밀번호 입력")
    void registerUser_InvalidPassword() {
        // given
        UserRegisterRequestDto requestDto = UserRegisterRequestDto.builder()
                .username("testuser1234")
                .password("short")
                .email("email@example.com")
                .build();

        // when, then
        assertThrows(InputMismatchException.class, () -> userService.registerUser(requestDto));
    }

    @Test
    @DisplayName("정상적인 사용자 탈퇴")
    void withdrawUser_Success() {
        // given
        UserRegisterRequestDto requestDto = UserRegisterRequestDto.builder()
                .username("testuser1234")
                .password("password123")
                .email("email@example.com")
                .build();
        userService.registerUser(requestDto);

        // when
        userService.withdrawUser("testuser1234", "password123");

        // then
        User foundUser = userRepository.findByUsername("testuser1234").orElse(null);
        assertNotNull(foundUser);
        assertEquals(UserStatusEnum.WITHDRAWN.getStatus(), foundUser.getStatusCode());
    }

    @Test
    @DisplayName("잘못된 비밀번호로 사용자 탈퇴 시도")
    void withdrawUser_PasswordMismatch() {
        // given
        UserRegisterRequestDto requestDto = UserRegisterRequestDto.builder()
                .username("testuser1234")
                .password("password123")
                .email("email@example.com")
                .build();
        userService.registerUser(requestDto);

        // when, then
        assertThrows(PasswordMismatchException.class, () -> userService.withdrawUser("testuser1234", "wrongpassword"));
    }

    @Test
    @DisplayName("이미 탈퇴한 사용자 탈퇴 재시도")
    void withdrawUser_AlreadyWithdrawn() {
        // given
        UserRegisterRequestDto requestDto = UserRegisterRequestDto.builder()
                .username("testuser1234")
                .password("password123")
                .email("email@example.com")
                .build();
        userService.registerUser(requestDto);
        userService.withdrawUser("testuser1234", "password123");

        // when, then
        assertThrows(UserWithdrawnException.class, () -> userService.withdrawUser("testuser1234", "password123"));
    }
}