package com.sparta.fifteen.service;

import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.entity.UserStatusEnum;
import com.sparta.fifteen.error.EmailAlreadyVerifiedException;
import com.sparta.fifteen.error.UserNotFoundException;
import com.sparta.fifteen.error.VerificationCodeExpiredException;
import com.sparta.fifteen.error.VerificationCodeMismatchException;
import com.sparta.fifteen.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class EmailVerificationService {
    private final UserRepository userRepository;
    private final EmailService emailService;

    public EmailVerificationService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public void sendVerificationEmail(User user) {
        emailService.sendEmail(user.getEmail(), "이메일 인증", "인증 코드: " + user.getEmailVerificationCode());
    }

    public String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    public void verifyEmail(String username, String verificationCode) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("사용자 ID가 존재하지 않습니다."));

        if (user.isEmailVerified()) {
            throw new EmailAlreadyVerifiedException("이메일이 이미 인증되었습니다.");
        }

        if (!user.getEmailVerificationCode().equals(verificationCode)) {
            throw new VerificationCodeMismatchException("인증 코드가 일치하지 않습니다.");
        }

        Timestamp sentAt = user.getEmailVerificationSendTime();
        if (sentAt == null || sentAt.before(new Timestamp(System.currentTimeMillis() - 180 * 1000))) {
            throw new VerificationCodeExpiredException("인증 코드가 만료되었습니다.");
        }

        user.setEmailVerified(true);
        user.setStatusCode(String.valueOf(UserStatusEnum.NORMAL.getStatus()));
        userRepository.save(user);
    }
}
