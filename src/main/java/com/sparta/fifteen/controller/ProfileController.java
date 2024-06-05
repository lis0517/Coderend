package com.sparta.fifteen.controller;

import com.sparta.fifteen.dto.ProfileRequestDto;
import com.sparta.fifteen.dto.ProfileResponseDto;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.security.UserDetailsImpl;
import com.sparta.fifteen.service.ProfileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<String> updateProfile(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody ProfileRequestDto ProfileRequestDto) {

        HttpHeaders headers = new HttpHeaders();

        try {
            User user = userDetails.getUser();

            profileService.updateProfile(user, ProfileRequestDto);

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
    public ResponseEntity<ProfileResponseDto> getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        HttpHeaders headers = new HttpHeaders();

        try {

            User user = userDetails.getUser();

            ProfileResponseDto userProfile = profileService.getUserProfile(user);

            if (userProfile == null) {
                headers.add("Message", "프로필이 존재하지 않습니다.");
                return new ResponseEntity<>(null, headers, HttpStatus.NOT_FOUND);
            }

            headers.add("Message", "프로필 조회에 성공하셨습니다.");
            return new ResponseEntity<>(userProfile, headers, HttpStatus.OK);

        }  catch (NotFoundException e) {
             headers.add("Message", "해당 사용자를 찾을 수 없습니다.");
             return new ResponseEntity<>(null, headers, HttpStatus.NOT_FOUND); // 사용자를 찾을 수 없는 경우에는 NOT_FOUND(404) 상태를 반환합니다.
        } catch (AccessDeniedException e ) {
             headers.add("Message", "인증되지 않은 사용자입니다.");
             return new ResponseEntity<>(null, headers, HttpStatus.UNAUTHORIZED); // 인증되지 않은 경우에는 UNAUTHORIZED(401) 상태를 반환합니다.
        } catch (Exception e) {
             headers.add("Message", "프로필 조회 중에 오류가 발생했습니다.");
            return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR); // 예외가 발생했을 때는 INTERNAL_SERVER_ERROR(500) 상태를 반환합니다.
        }
    }




}
