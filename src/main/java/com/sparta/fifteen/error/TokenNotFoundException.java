package com.sparta.fifteen.error;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String message){
        super(message);
    }

    public TokenNotFoundException(ExceptionMessage message){
        super(message.getMessage());
    }
}
