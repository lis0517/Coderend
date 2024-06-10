package com.sparta.fifteen.service;

import com.sparta.fifteen.entity.EmailVerification;
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
        emailService.sendEmail(user.getEmail(), "이메일 인증", "인증 코드: " + user.getEmailVerification().getEmailVerificationCode());
    }


    public String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    public void verifyEmail(String username, String verificationCode) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("사용자 ID가 존재하지 않습니다."));

        if (user.getStatusCode().equals(String.valueOf(UserStatusEnum.NORMAL.getStatus()))) {
            throw new EmailAlreadyVerifiedException("이메일이 이미 인증되었습니다.");
        }

        if (!user.getEmailVerification().getEmailVerificationCode().equals(verificationCode)) {
            throw new VerificationCodeMismatchException("인증 코드가 일치하지 않습니다.");
        }

        Timestamp sentAt = user.getEmailVerification().getEmailVerificationSendTime();
        if (sentAt == null || sentAt.before(new Timestamp(System.currentTimeMillis() - 180 * 1000))) {
            resendVerificationEmail(user);
            throw new VerificationCodeExpiredException("인증 코드가 만료되었습니다. 새로운 인증 코드를 입력해주세요.");
        }

        // 인증 완료 후 이메일 인증 정보만 삭제
        user.setEmailVerification(null);

        user.setStatusCode(String.valueOf(UserStatusEnum.NORMAL.getStatus()));
        userRepository.save(user);
    }

    public void resendVerificationEmail(User user) {
        // 새로운 인증 코드 생성 및 설정
        String newVerificationCode = generateVerificationCode();
        user.getEmailVerification().setEmailVerificationCode(newVerificationCode);
        user.getEmailVerification().setEmailVerificationSendTime(new Timestamp(System.currentTimeMillis()));

        // 사용자 저장
        userRepository.save(user);

        // 인증 이메일 재전송
        sendVerificationEmail(user);
    }
}
