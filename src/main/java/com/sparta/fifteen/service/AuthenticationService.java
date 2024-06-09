package com.sparta.fifteen.service;

import com.sparta.fifteen.dto.UserRequestDto;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.entity.UserStatusEnum;
import com.sparta.fifteen.entity.token.RefreshToken;
import com.sparta.fifteen.error.PasswordMismatchException;
import com.sparta.fifteen.error.UserWithdrawnException;
import com.sparta.fifteen.repository.UserRepository;
import com.sparta.fifteen.service.token.LogoutAccessTokenService;
import com.sparta.fifteen.service.token.RefreshTokenService;
import com.sparta.fifteen.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        Optional<User> optionalUser = userRepository.findByUsername(requestDto.getUsername());

        if (optionalUser.isPresent()) {
            User registeredUser = optionalUser.get();
            if (registeredUser.getStatusCode().equals(String.valueOf(UserStatusEnum.WITHDRAWN.getStatus()))) {
                throw new UserWithdrawnException("탈퇴한 계정");
            }

            if (passwordEncoder.matches(requestDto.getPassword(), registeredUser.getPassword())) {
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

