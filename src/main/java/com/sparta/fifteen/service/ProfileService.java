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

    public void updateProfile(User user, ProfileRequestDto profileRequestDto) {

        if(!profileRequestDto.isPasswordMatching()){
            throw new PasswordExpiredException("새로운 패스워드와 현재 비밀번호가 동일합니다.");
        }
        if(!profileRequestDto.isNewPasswordMatch()) {
            throw new PasswordExpiredException("새로운 패스워드와 확인 패스워드가 일치 하지 않습니다.");
        }

        user.updateProfile(profileRequestDto);
        userRepository.save(user);
    }

    @Transactional
    public  ProfileResponseDto getUserProfile(User user) {


        return new ProfileResponseDto(user);
    }

}