package com.sparta.fifteen.dto;

import com.sparta.fifteen.entity.User;

public class ProfileResponseDto {
    private String username;
    private String name;
    private String oneline;
    private String email;

    public ProfileResponseDto(User user) {
        this.username = user.getUsername();
        this.name = user.getName();
        this.oneline = user.getOneLine();
        this.email = user.getEmail();
    }

}
