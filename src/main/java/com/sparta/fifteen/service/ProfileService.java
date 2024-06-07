package com.sparta.fifteen.service;

import com.mysql.cj.exceptions.PasswordExpiredException;
import com.sparta.fifteen.dto.ProfileRequestDto;
import com.sparta.fifteen.dto.ProfileResponseDto;
import com.sparta.fifteen.entity.User;

import com.sparta.fifteen.repository.UserRepository;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.InputMismatchException;

@Service
public class ProfileService {

    UserRepository userRepository;


    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void updateProfile(String username, ProfileRequestDto profileRequestDto) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new InputMismatchException("사용자가 없습니다."));
        System.out.println(profileRequestDto.getName());
        System.out.println(profileRequestDto.getOneline());
        System.out.println(profileRequestDto.getNewPassword());
        System.out.println(profileRequestDto.getCheckNewPassword());
        System.out.println(profileRequestDto.getCheckNewPassword());

        if(profileRequestDto.isPasswordMatching()){
            throw new PasswordExpiredException("New Password equal Current Password");
        }
        if(!profileRequestDto.isNewPasswordMatch()) {
            throw new PasswordExpiredException("New Password not equal Check New Password");
        }

        user.updateProfile(profileRequestDto);
        System.out.println(user.getName());
        System.out.println(user.getPassword());
        System.out.println(user.getModifiedOn());
        userRepository.save(user);
    }

    @Transactional
    public  ProfileResponseDto getUserProfile(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new InputMismatchException("사용자가 없습니다."));

        return new ProfileResponseDto(user);
    }

}