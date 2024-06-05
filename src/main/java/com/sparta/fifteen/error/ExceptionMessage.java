package com.sparta.fifteen.error;

public enum ExceptionMessage {

    TOKEN_NOT_FOUND("토큰을 찾을 수 없습니다.");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

}
