package com.sparta.fifteen.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRequestDto {
    private String username;
    private String password;
}
