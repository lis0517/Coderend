package com.sparta.fifteen.service;

import com.sparta.fifteen.dto.ProfileRequestDto;
import com.sparta.fifteen.dto.ProfileResponseDto;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.error.PasswordMismatchException;
import com.sparta.fifteen.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.InputMismatchException;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public ProfileService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Transactional
    public void updateProfile(String username, ProfileRequestDto profileRequestDto) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new InputMismatchException("사용자가 없습니다."));


        if(profileRequestDto.isPasswordMatching()){


            throw new PasswordMismatchException("New Password equal Current Password");
        }


        if(!profileRequestDto.isNewPasswordMatch()) {

            throw new PasswordMismatchException("New Password not equal Check New Password");
        }


        user.updateProfile(profileRequestDto);

        user.updatePassword(passwordEncoder.encode(profileRequestDto.getNewPassword()));

        userRepository.save(user);
    }

    @Transactional
    public  ProfileResponseDto getUserProfile(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new InputMismatchException("사용자가 없습니다."));

        return new ProfileResponseDto(user);
    }

}