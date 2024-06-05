package com.sparta.fifteen.dto;

import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.entity.UserRefreshToken;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class UserRegisterResponseDto {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String oneLine;
    private UserRefreshToken userRefreshToken;
    private String statusCode;
    private Timestamp statusChangedTime; // null 값이 들어감 후에 다듬을 예정 -> 주석처리 하면 responseDto에서 보이지 않음
    private Timestamp createdOn;
    private Timestamp modifiedOn; // null 값이 들어감 후에 다듬을 예정 -> 주석처리 하면 responseDto에서 보이지 않음

    public UserRegisterResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.email = user.getEmail();
        this.oneLine = user.getOneLine();
        this.userRefreshToken = user.getUserRefreshToken();
        this.statusCode = user.getStatusCode();
        this.createdOn = user.getCreatedOn();
    }
}
