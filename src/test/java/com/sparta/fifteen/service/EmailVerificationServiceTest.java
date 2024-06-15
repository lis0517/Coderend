package com.sparta.fifteen.service;

import com.sparta.fifteen.entity.EmailVerification;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.entity.UserStatusEnum;
import com.sparta.fifteen.error.EmailAlreadyVerifiedException;
import com.sparta.fifteen.error.UserNotFoundException;
import com.sparta.fifteen.error.VerificationCodeExpiredException;
import com.sparta.fifteen.error.VerificationCodeMismatchException;
import com.sparta.fifteen.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Test
    @DisplayName("인증 코드 생성")
    void generateVerificationCode() {
        // when
        String verificationCode = emailVerificationService.generateVerificationCode();

        // then
        assertNotNull(verificationCode);
        assertEquals(6, verificationCode.length());
    }

    @Test
    @DisplayName("이메일 인증 성공")
    void verifyEmail_Success() {
        // given
        String username = "testuser";
        String verificationCode = "123456";
        User user = User.builder().username(username).statusCode(String.valueOf(UserStatusEnum.PENDING.getStatus())).build();
        EmailVerification emailVerification = new EmailVerification();
        emailVerification.setEmailVerificationCode(verificationCode);
        emailVerification.setEmailVerificationSendTime(new Timestamp(System.currentTimeMillis()));
        user.updateEmailVerification(emailVerification);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // when
        emailVerificationService.verifyEmail(username, verificationCode);

        // then
        assertEquals(String.valueOf(UserStatusEnum.NORMAL.getStatus()), user.getStatusCode());
        assertNull(user.getEmailVerification());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("이메일 인증 실패 - 사용자 ID 존재하지 않음")
    void verifyEmail_UserNotFound() {
        // given
        String username = "nonexistent";
        String verificationCode = "123456";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // when, then
        assertThrows(UserNotFoundException.class, () -> emailVerificationService.verifyEmail(username, verificationCode));
    }

    @Test
    @DisplayName("이메일 인증 실패 - 이메일 이미 인증됨")
    void verifyEmail_EmailAlreadyVerified() {
        // given
        String username = "testuser";
        String verificationCode = "123456";
        User user = User.builder().username(username).statusCode(String.valueOf(UserStatusEnum.NORMAL.getStatus())).build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // when, then
        assertThrows(EmailAlreadyVerifiedException.class, () -> emailVerificationService.verifyEmail(username, verificationCode));
    }

    @Test
    @DisplayName("이메일 인증 실패 - 인증 코드 불일치")
    void verifyEmail_VerificationCodeMismatch() {
        // given
        String username = "testuser";
        String verificationCode = "123456";
        User user = User.builder().username(username).statusCode(String.valueOf(UserStatusEnum.PENDING.getStatus())).build();
        EmailVerification emailVerification = new EmailVerification();
        emailVerification.setEmailVerificationCode("654321");
        user.updateEmailVerification(emailVerification);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // when, then
        assertThrows(VerificationCodeMismatchException.class, () -> emailVerificationService.verifyEmail(username, verificationCode));
    }

    @Test
    @DisplayName("이메일 인증 실패 - 인증 코드 만료")
    void verifyEmail_VerificationCodeExpired() {
        // given
        String username = "testuser";
        String verificationCode = "123456";
        User user = User.builder().username(username).statusCode(String.valueOf(UserStatusEnum.PENDING.getStatus())).build();
        EmailVerification emailVerification = new EmailVerification();
        emailVerification.setEmailVerificationCode(verificationCode);
        emailVerification.setEmailVerificationSendTime(new Timestamp(System.currentTimeMillis() - 200 * 1000)); // 3분 이상 경과
        user.updateEmailVerification(emailVerification);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // when, then
        assertThrows(VerificationCodeExpiredException.class, () -> emailVerificationService.verifyEmail(username, verificationCode));
        verify(emailService, times(1)).sendEmail(eq(user.getEmail()), anyString(), anyString());
    }
}