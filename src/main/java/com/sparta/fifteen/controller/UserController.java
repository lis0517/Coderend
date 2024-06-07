package com.sparta.fifteen.controller;

import com.sparta.fifteen.config.JwtConfig;
import com.sparta.fifteen.dto.UserRequestDto;
import com.sparta.fifteen.dto.UserRegisterRequestDto;
import com.sparta.fifteen.dto.UserRegisterResponseDto;
import com.sparta.fifteen.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.InputMismatchException;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    private ResponseEntity<?> signup(@RequestBody UserRegisterRequestDto requestDto) {
        try {
            UserRegisterResponseDto responseDto = userService.registerUser(requestDto);
            return ResponseEntity.ok().body(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("이미 존재하는 User ID 입니다. 회원가입에 실패하셨습니다.");
        } catch (InputMismatchException e) {
            return ResponseEntity.badRequest().body("잘못된 비밀번호 형식입니다. 회원가입에 실패하셨습니다.");
        }
    }

    @PostMapping("/login")
    private ResponseEntity<?> userLogin(@RequestBody UserRequestDto requestDto) {
        try {
            String token = userService.loginUser(requestDto);
            return ResponseEntity.ok().header(JwtConfig.staticHeader, JwtConfig.staticTokenPrefix + token).body(token);
        } catch (InputMismatchException e) {
            return ResponseEntity.badRequest().body("아이디 또는 비밀번호를 확인해주세요. 로그인에 실패하셨습니다.");
        }
    }

    @PostMapping("/logout")
    private ResponseEntity<?> userLogout(@RequestBody UserRequestDto requestDto) {
        try {
            // UserDetails에서 사용자 이름 가져오기
            String userDetailsUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            // requestDto에서 사용자 이름 가져오기
            String requestDtoUsername = requestDto.getUsername();

            // 사용자 이름이 일치하는지 확인
            if (userDetailsUsername.equals(requestDtoUsername)) {
                // 로그아웃 실행
                userService.logoutUser(requestDtoUsername);
            }
            return ResponseEntity.ok().body("로그아웃되었습니다.");
        } catch (InputMismatchException e) {
            return ResponseEntity.badRequest().body("아이디 또는 비밀번호를 확인해주세요. 로그인에 실패하셨습니다.");
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request){
        String newAccessToken = userService.refreshToken(request);
        if (newAccessToken != null){
            return ResponseEntity.ok()
                    .header(JwtConfig.staticHeader, JwtConfig.staticTokenPrefix + newAccessToken)
                    .body(newAccessToken);
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token.");
        }
    }

    @PutMapping("/withdraw")
    public ResponseEntity<?> userWithdraw(@RequestBody UserRequestDto requestDto) {
        try {
            // UserDetails에서 사용자 이름 가져오기
            String userDetailsUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            // requestDto에서 사용자 이름 가져오기
            String requestDtoUsername = requestDto.getUsername();

            // 사용자 이름이 일치하는지 확인
            if (userDetailsUsername.equals(requestDtoUsername)) {
                userService.withdrawUser(requestDtoUsername, requestDto.getPassword());
            }
            return ResponseEntity.ok().body("회원 탈퇴.");
        } catch (InputMismatchException e) {
            return ResponseEntity.badRequest().body("아이디 또는 비밀번호를 확인해주세요.");
        }
    }
}
