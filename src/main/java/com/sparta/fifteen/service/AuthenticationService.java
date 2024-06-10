package com.sparta.fifteen.service;

import com.sparta.fifteen.dto.UserRequestDto;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.entity.UserStatusEnum;
import com.sparta.fifteen.entity.RefreshToken;
import com.sparta.fifteen.error.PasswordMismatchException;
import com.sparta.fifteen.error.UserWithdrawnException;
import com.sparta.fifteen.repository.UserRepository;
import com.sparta.fifteen.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final LogoutAccessTokenService logoutAccessTokenService;

    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 RefreshTokenService refreshTokenService,
                                 LogoutAccessTokenService logoutAccessTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.logoutAccessTokenService = logoutAccessTokenService;
    }

    public String loginUser(UserRequestDto requestDto) {
        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        if (user.getStatusCode().equals(String.valueOf(UserStatusEnum.WITHDRAWN.getStatus()))) {
            throw new UserWithdrawnException("탈퇴한 계정입니다.");
        }

//        if (user.getStatusCode().equals(String.valueOf(UserStatusEnum.PENDING.getStatus()))) {
//            throw new UserPendingException("인증 대기 상태입니다. 이메일 인증을 해주세요.");
//        }

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new PasswordMismatchException("패스워드가 일치하지 않습니다.");
        }

        if (passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            String accessToken = JwtTokenProvider.generateAccessToken(requestDto.getUsername());
            RefreshToken refreshToken = refreshTokenService.updateRefreshToken(user);

            userRepository.save(user);
            JwtTokenProvider.setRefreshTokenAtCookie(refreshToken);

            return accessToken;
        }
        return null;
    }

    public void logoutUser(String token, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        refreshTokenService.deleteByUser(user);
        logoutAccessTokenService.saveLogoutAccessToken(token, username);
    }

    public String refreshToken(HttpServletRequest request) {
        String token = JwtTokenProvider.getRefreshTokenFromRequest(request);
        if (token != null && !JwtTokenProvider.isTokenExpired(token)) {
            User user = refreshTokenService.findUserByToken(token);
            return JwtTokenProvider.generateAccessToken(user.getUsername());
        }
        return null;
    }
}

