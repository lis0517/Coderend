package com.sparta.fifteen.controller;

import com.sparta.fifteen.dto.EmailVerifyRequestDto;
import com.sparta.fifteen.error.EmailAlreadyVerifiedException;
import com.sparta.fifteen.error.UserNotFoundException;
import com.sparta.fifteen.error.VerificationCodeExpiredException;
import com.sparta.fifteen.error.VerificationCodeMismatchException;
import com.sparta.fifteen.service.EmailService;
import com.sparta.fifteen.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EmailController {

    private final EmailService emailService;
    private final UserService userService;

    public EmailController(EmailService emailService, UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestBody EmailVerifyRequestDto requestDto) {
        try {
            userService.verifyEmail(requestDto.getUsername(), requestDto.getVerificationCode());
            return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
        } catch (EmailAlreadyVerifiedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (VerificationCodeMismatchException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (VerificationCodeExpiredException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }
}

