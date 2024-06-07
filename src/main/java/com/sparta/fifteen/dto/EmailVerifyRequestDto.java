package com.sparta.fifteen.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerifyRequestDto {
    private String username;
    private String verificationCode;
}
