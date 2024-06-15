package com.sparta.fifteen.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Test
    @DisplayName("이메일 전송")
    void sendEmail() {
        // given
        String email = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Text";

        // when
        emailService.sendEmail(email, subject, text);

        // then
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}