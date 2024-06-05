package com.sparta.fifteen.service;

import com.sparta.fifteen.dto.ProfileRequestDto;
import com.sparta.fifteen.dto.ProfileResponseDto;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.repository.UserRepository;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    UserRepository userRepository;


    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void updateProfile(User user, ProfileRequestDto profileRequestDto) {

        if(!profileRequestDto.isNewPasswordMatch()) {
            return;
        }

        user.updateProfile(profileRequestDto);
        userRepository.save(user);
    }

    @Transactional
    public  ProfileResponseDto getUserProfile(User user) {

        if (user == null) {
            return null;
        }

        return new ProfileResponseDto(user);
    }


}
