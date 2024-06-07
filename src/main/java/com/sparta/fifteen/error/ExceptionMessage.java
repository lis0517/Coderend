package com.sparta.fifteen.error;

import lombok.Getter;

@Getter
public enum ExceptionMessage {

    TOKEN_NOT_FOUND("토큰을 찾을 수 없습니다.");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }
}