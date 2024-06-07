package com.sparta.fifteen.service;

import com.sparta.fifteen.dto.UserRegisterRequestDto;
import com.sparta.fifteen.dto.UserRegisterResponseDto;
import com.sparta.fifteen.entity.EmailVerification;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.entity.UserStatusEnum;
import com.sparta.fifteen.error.PasswordMismatchException;
import com.sparta.fifteen.error.UserAlreadyExistsException;
import com.sparta.fifteen.error.UserNotFoundException;
import com.sparta.fifteen.error.UserWithdrawnException;
import com.sparta.fifteen.repository.UserRepository;
import com.sparta.fifteen.service.token.LogoutAccessTokenService;
import com.sparta.fifteen.service.token.RefreshTokenService;
import com.sparta.fifteen.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.sql.Timestamp;
import java.util.InputMismatchException;
import java.util.Optional;

@Service
@Validated
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final EmailVerificationService emailVerificationService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RefreshTokenService refreshTokenService,
                       EmailVerificationService emailVerificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.logoutAccessTokenService = logoutAccessTokenService;
        this.emailService = emailService;
    }

    public UserRegisterResponseDto registerUser(UserRegisterRequestDto requestDto) {
        // username 유효성 검사
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            User existingUser = userRepository.findByUsername(requestDto.getUsername())
                    .orElseThrow(() -> new UserNotFoundException("사용자 ID가 존재하지 않습니다."));
            // 탈퇴한 사용자 확인
            if (existingUser.getStatusCode().equals(String.valueOf(UserStatusEnum.WITHDRAWN.getStatus()))) {
                throw new UserWithdrawnException("탈퇴한 ID는 재사용할 수 없습니다.");
            }
            throw new UserAlreadyExistsException("이미 존재하는 ID");
        }
        // password 유효성 검사
        if (requestDto.getPassword().length() < 10) {
            throw new InputMismatchException("잘못된 비밀번호 형식");
        }
        // 사용자 초기화 및 저장
        User user = initializeUser(requestDto);
        userRepository.save(user);

        // 이메일 발송
        // TODO : 현재 막아둔 상태로 추후 유효성 검사 추가 및 주석 해제
        // sendVerificationEmail(user);
        return new UserRegisterResponseDto(user);
    }

    public void withdrawUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("사용자 ID가 존재하지 않습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }

        if (user.getStatusCode().equals(String.valueOf(UserStatusEnum.WITHDRAWN.getStatus()))) {
            throw new UserWithdrawnException("이미 탈퇴한 사용자입니다.");
        }

        user.setStatusCode(String.valueOf(UserStatusEnum.WITHDRAWN.getStatus()));
        user.setModifiedOn(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);

        refreshTokenService.deleteByUser(user);
    }

    private User initializeUser(UserRegisterRequestDto requestDto) {
        User user = new User(requestDto);
        user.setCreatedOn(new Timestamp(System.currentTimeMillis()));
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setStatusCode(String.valueOf(UserStatusEnum.PENDING.getStatus()));

        // EmailVerification 엔티티 생성 및 설정
        EmailVerification emailVerification = new EmailVerification();
        emailVerification.setUser(user);
        String verificationCode = emailVerificationService.generateVerificationCode();
        emailVerification.setEmailVerificationCode(verificationCode);
        emailVerification.setEmailVerificationSendTime(new Timestamp(System.currentTimeMillis()));
        user.setEmailVerification(emailVerification); // User 엔티티와의 관계 설정
        return user;
    }

    private String generateVerificationCode() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }

    private void sendVerificationEmail(User user) {
        emailService.sendEmail(user.getEmail(), "이메일 인증", "인증 코드: " + user.getEmailVerificationCode());
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
        user.setStatusCode(String.valueOf(UserStatusEnum.NORMAL.getStatus())); // 인증 완료 상태
        userRepository.save(user);
    }

    public long getUserIdByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(()->new IllegalArgumentException("사용자가 존재하지 않습니다.")).getId();
    }
}