package com.sparta.fifteen.service;

import com.sparta.fifteen.dto.ProfileRequestDto;
import com.sparta.fifteen.dto.ProfileResponseDto;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.error.PasswordMismatchException;
import com.sparta.fifteen.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ProfileService profileService;

    @Test
    @DisplayName("프로필 수정 성공")
    void updateProfile_Success() {
        // given
        String username = "testuser1234";
        ProfileRequestDto requestDto = ProfileRequestDto.builder()
                .currentPassword("abcd1234")
                .newPassword("newabcd1234")
                .checkNewPassword("newabcd1234")
                .build();
        User user = User.builder().username(username).password(passwordEncoder.encode("abcd1234")).build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(requestDto.getNewPassword())).thenReturn("encodedNewPassword");

        // when
        profileService.updateProfile(username, requestDto);

        // then
        verify(userRepository, times(1)).save(user);
        assertEquals("encodedNewPassword", user.getPassword());
    }

    @Test
    @DisplayName("프로필 수정 실패 - 새 비밀번호와 현재 비밀번호 일치")
    void updateProfile_NewPasswordEqualsCurrentPassword() {
        // given
        String username = "testuser1234";
        ProfileRequestDto requestDto = ProfileRequestDto.builder()
                .currentPassword("abcd1234")
                .newPassword("abcd1234")
                .checkNewPassword("abcd1234")
                .build();
        User user = User.builder().username(username).password(passwordEncoder.encode("abcd1234")).build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // when, then
        assertThrows(PasswordMismatchException.class, () -> profileService.updateProfile(username, requestDto));
    }

    @Test
    @DisplayName("프로필 수정 실패 - 새 비밀번호와 새 비밀번호 확인 불일치")
    void updateProfile_NewPasswordNotEqualsCheckNewPassword() {
        // given
        String username = "testuser1234";
        ProfileRequestDto requestDto = ProfileRequestDto.builder()
                .currentPassword("abcd1234")
                .newPassword("newabcd1234")
                .checkNewPassword("wrongabcd1234")
                .build();
        User user = User.builder().username(username).password(passwordEncoder.encode("abcd1234")).build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // when, then
        assertThrows(PasswordMismatchException.class, () -> profileService.updateProfile(username, requestDto));
    }

    @Test
    @DisplayName("사용자 프로필 정보 조회 성공")
    void getUserProfile_Success() {
        // given
        String username = "testuser1234";
        User user = User.builder().username(username).name("Test User").email("test@example.com").build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // when
        ProfileResponseDto responseDto = profileService.getUserProfile(username);

        // then
        assertEquals(username, responseDto.getUsername());
        assertEquals("Test User", responseDto.getName());
        assertEquals("test@example.com", responseDto.getEmail());
    }
}