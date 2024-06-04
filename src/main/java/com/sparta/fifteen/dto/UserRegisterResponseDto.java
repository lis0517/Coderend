package com.sparta.fifteen.dto;

import com.sparta.fifteen.entity.User;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class UserRegisterResponseDto {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String oneLine;
    private String refreshToken;
    private String statusCode;
    private Timestamp statusChangedTime;
    private Timestamp createdOn;
    private Timestamp modifiedOn;

    public UserRegisterResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.email = user.getEmail();
        this.oneLine = user.getOneLine();
        this.refreshToken = user.getRefreshToken();
        this.statusCode = user.getStatusCode();
        this.createdOn = user.getCreatedOn();
    }
}
