package com.sparta.fifteen.service;

import com.sparta.fifteen.dto.UserRegisterRequestDto;
import com.sparta.fifteen.dto.UserRegisterResponseDto;
import com.sparta.fifteen.dto.UserRequestDto;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.entity.UserStatusEnum;
import com.sparta.fifteen.entity.token.RefreshToken;
import com.sparta.fifteen.error.*;
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

    private final LogoutAccessTokenService logoutAccessTokenService;

    private final EmailService emailService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RefreshTokenService refreshTokenService,
                       LogoutAccessTokenService logoutAccessTokenService,
                       EmailService emailService) {
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
        sendVerificationEmail(user);
        return new UserRegisterResponseDto(user);
    }

    public String loginUser(UserRequestDto requestDto) {
        // DB에서 username 먼저 조회
        Optional<User> optionalUser = userRepository.findByUsername(requestDto.getUsername());

        if(optionalUser.isPresent()) {
            User registeredUser = optionalUser.get();
            // User 상태가 WITHDRAW면 예외 처리
            if(registeredUser.getStatusCode().equals(String.valueOf(UserStatusEnum.WITHDRAWN.getStatus()))) {
                throw new UserWithdrawnException("탈퇴한 계정");
            }
            // 현재 상태 인증 대기라면 예외처리
            // 토큰 생성 위치
            if(passwordEncoder.matches(requestDto.getPassword(), registeredUser.getPassword())) {
                String accessToken = JwtTokenProvider.generateAccessToken(requestDto.getUsername());
                RefreshToken refreshToken = refreshTokenService.updateRefreshToken(registeredUser);

                userRepository.save(registeredUser);
                JwtTokenProvider.setRefreshTokenAtCookie(refreshToken);

                return accessToken;
            }
        }
        throw new PasswordMismatchException("아이디, 패스워드 불일치");
    }

    public void logoutUser(String token, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        refreshTokenService.deleteByUser(user);

        logoutAccessTokenService.saveLogoutAccessToken(token, username);
        //if (optionalUser.isPresent()) {
        //    User user = optionalUser.get();
        //    System.out.println("User found: " + user.getUsername());

        //user.setUserRefreshToken(null); // todo : 없는 채로 테스트 해보기
        //    userRepository.save(user); // 변경사항 저장
        // RefreshToken 제거
        //    refreshTokenService.deleteByUser(user);
        //} else {
        //    System.out.println("User not found");
        //}
    }

    public String refreshToken(HttpServletRequest request){
        String token = JwtTokenProvider.getRefreshTokenFromRequest(request);
        if (token != null && !JwtTokenProvider.isTokenExpired(token)){
            // refresh token으로 user 얻어오기
            User user = refreshTokenService.findUserByToken(token);
            // 새로운 access token 생성
            return JwtTokenProvider.generateAccessToken(user.getUsername());
        }
        return null;
    }

    public void withdrawUser(String username, String password) {
        // 사용자 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("사용자 ID가 존재하지 않습니다."));
        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }
        // 이미 탈퇴한 사용자 확인
        if (user.getStatusCode().equals(String.valueOf(UserStatusEnum.WITHDRAWN.getStatus()))) {
            throw new UserWithdrawnException("이미 탈퇴한 사용자입니다.");
        }

        // 상태 코드 변경
        user.setStatusCode(String.valueOf(UserStatusEnum.WITHDRAWN.getStatus()));
        user.setModifiedOn(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);
        System.out.println("여기까지는 왔다");
        // refresh token 무효화
        refreshTokenService.deleteByUser(user);
    }

    private User initializeUser (UserRegisterRequestDto requestDto) {
        User user = new User(requestDto);
        user.setCreatedOn(new Timestamp(System.currentTimeMillis()));
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setStatusCode(String.valueOf(UserStatusEnum.PENDING.getStatus())); // 인증 전 상태

        String verificationCode = generateVerificationCode();
        user.setEmailVerificationCode(verificationCode);
        user.setEmailVerificationSendTime(new Timestamp(System.currentTimeMillis()));
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
}