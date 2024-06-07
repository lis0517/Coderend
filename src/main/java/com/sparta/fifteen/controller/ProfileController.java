package com.sparta.fifteen.controller;

import com.sparta.fifteen.config.JwtConfig;
import com.sparta.fifteen.dto.ProfileRequestDto;
import com.sparta.fifteen.dto.ProfileResponseDto;
import com.sparta.fifteen.service.ProfileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

@RestController
@RequestMapping("/api/user")
@Validated
public class ProfileController {

    ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestHeader("Authorization") String authorizationHeader, @RequestBody ProfileRequestDto ProfileRequestDto) {

        HttpHeaders headers = new HttpHeaders();

        try {
            String accessToken = authorizationHeader.replace(JwtConfig.staticTokenPrefix, "");
            // UserDetails에서 사용자 이름 가져오기
            String username = SecurityContextHolder.getContext().getAuthentication().getName();


            profileService.updateProfile(username, ProfileRequestDto);

            return ResponseEntity.ok().body("프로필 수정에 성공하였습니다.");

        } catch (NotFoundException e) {
            headers.add("Message", "해당 사용자를 찾을 수 없습니다.");
            return new ResponseEntity<>(null, headers, HttpStatus.NOT_FOUND); // 사용자를 찾을 수 없는 경우에는 NOT_FOUND(404) 상태를 반환합니다.
        } catch (AccessDeniedException e ) {
            headers.add("Message", "인증되지 않은 사용자입니다.");
            return new ResponseEntity<>(null, headers, HttpStatus.UNAUTHORIZED); // 인증되지 않은 경우에는 UNAUTHORIZED(401) 상태를 반환합니다.
        } catch (Exception e) {
            headers.add("Message", "프로필 수정 중에 오류가 발생했습니다.");
            return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR); // 예외가 발생했을 때는 INTERNAL_SERVER_ERROR(500) 상태를 반환합니다.
        }

    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDto> getProfile(@RequestHeader("Authorization") String authorizationHeader) {

        HttpHeaders headers = new HttpHeaders();

        try {
            String accessToken = authorizationHeader.replace(JwtConfig.staticTokenPrefix, "");
            // UserDetails에서 사용자 이름 가져오기
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            ProfileResponseDto userProfile = profileService.getUserProfile(username);

            if (userProfile == null) {
                headers.add("Message", "프로필이 존재하지 않습니다.");
                return ResponseEntity.notFound().headers(headers).build();
            }

            headers.add("Message", "프로필 조회에 성공하셨습니다.");
            return ResponseEntity.ok().headers(headers).body(userProfile);

        } catch (NotFoundException e) {
            headers.add("Message", "해당 사용자를 찾을 수 없습니다.");
            return ResponseEntity.notFound().headers(headers).build();
        } catch (AccessDeniedException e) {
            headers.add("Message", "인증되지 않은 사용자입니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(headers).build();
        } catch (RuntimeException e) {
            String errorMessage = "프로필 조회 중에 오류가 발생했습니다: " + e.getMessage();
            headers.add("Message", errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).build();
        }
    }

}
