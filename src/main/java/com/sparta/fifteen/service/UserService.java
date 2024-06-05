package com.sparta.fifteen.service;

import com.sparta.fifteen.dto.UserLoginRequestDto;
import com.sparta.fifteen.entity.UserRefreshToken;
import com.sparta.fifteen.entity.UserStatusEnum;
import com.sparta.fifteen.dto.UserRegisterRequestDto;
import com.sparta.fifteen.dto.UserRegisterResponseDto;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.repository.UserRepository;
import com.sparta.fifteen.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
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

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserRegisterResponseDto registerUser(UserRegisterRequestDto requestDto) {
        // username 유효성 검사
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 ID");
        }
        // password 유효성 검사
        if (requestDto.getPassword().length() < 10) {
            throw new InputMismatchException("잘못된 비밀번호 형식");
        }
        User user = new User(requestDto);
        user.setCreatedOn(new Timestamp(System.currentTimeMillis()));
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setStatusCode(String.valueOf(UserStatusEnum.NORMAL.getStatus()));
        userRepository.save(user);
        return new UserRegisterResponseDto(user);
    }

    public String loginUser(UserLoginRequestDto requestDto, HttpServletResponse response) {
        // DB에서 username 먼저 조회
        Optional<User> optionalUser = userRepository.findByUsername(requestDto.getUsername());

        if(optionalUser.isPresent()) {
            User registeredUser = optionalUser.get();
            // User 상태가 WITHDRAW면 예외 처리
            if(registeredUser.getStatusCode().equals(String.valueOf(UserStatusEnum.WITHDRAWN.getStatus()))) {
                throw new IllegalArgumentException("탈퇴한 계정");
            }
            // 토큰 생성 위치
            if(passwordEncoder.matches(requestDto.getPassword(), registeredUser.getPassword())) {
                String accessToken = JwtTokenProvider.generateAccessToken(requestDto.getUsername());
                String refreshToken = JwtTokenProvider.generateRefreshToken();

                UserRefreshToken userRefreshToken = registeredUser.getUserRefreshToken();
                if(userRefreshToken == null) {
                    userRefreshToken = new UserRefreshToken(refreshToken, registeredUser);
                    registeredUser.setUserRefreshToken(userRefreshToken);
                } else {
                    userRefreshToken.updateRefreshToken(refreshToken);
                }
                userRepository.save(registeredUser);

                // Header에 accessToken 추가
                response.setHeader("Authorization", "Bearer " + accessToken);

                //
                return "성공";
            }
        }
        throw new InputMismatchException("아이디, 패스워드 불일치");
    }
}
