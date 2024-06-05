package com.sparta.fifteen.controller;

import com.sparta.fifteen.dto.ProfileRequestDto;
import com.sparta.fifteen.dto.ProfileResponseDto;
import com.sparta.fifteen.service.ProfileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Validated
public class ProfileController {

    ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestHeader("Authorization") String token,@RequestBody ProfileRequestDto ProfileRequestDto) {


        try {
            Long userId = getUserIdFromToken(token);

            profileService.updateProfile(userId, ProfileRequestDto);

            return ResponseEntity.ok().body("프로필 수정에 성공하였습니다.");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("프로필 수정에 실패하였습니다.");
        }

    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDto> getProfile(@RequestHeader("Authorization") String token) {
        HttpHeaders headers = new HttpHeaders();

        if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
            headers.add("Message", "프로필 조회에 실패하셨습니다.");
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }

        try {

            Long userId = getUserIdFromToken(token);

            ProfileResponseDto userProfile = profileService.getUserProfile(userId);

            if (userProfile == null) {
                headers.add("Message", "프로필 조회에 실패하였습니다.");
                return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
            }

            headers.add("Message", "프로필 조회에 성공하셨습니다.");
            return new ResponseEntity<>(userProfile, headers, HttpStatus.OK);

        } catch (Exception e) {
            headers.add("Message", "프로필 조회에 실패하였습니다.");
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }
    }

    private Long getUserIdFromToken(String token) {

        return 1L;
    }

}
