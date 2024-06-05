package com.sparta.fifteen.controller;

import com.sparta.fifteen.dto.ProfileRequestDto;
import com.sparta.fifteen.dto.ProfileResponseDto;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.security.UserDetailsImpl;
import com.sparta.fifteen.service.ProfileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<String> updateProfile(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody ProfileRequestDto ProfileRequestDto) {


        try {
            User user = userDetails.getUser();

            profileService.updateProfile(user, ProfileRequestDto);

            return ResponseEntity.ok().body("프로필 수정에 성공하였습니다.");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("프로필 수정에 실패하였습니다.");
        }

    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDto> getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        HttpHeaders headers = new HttpHeaders();

        try {

            User user = userDetails.getUser();

            ProfileResponseDto userProfile = profileService.getUserProfile(user);

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


}
