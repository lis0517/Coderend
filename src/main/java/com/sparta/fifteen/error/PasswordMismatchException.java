package com.sparta.fifteen.error;

public class PasswordMismatchException extends RuntimeException{

    public PasswordMismatchException(String message) {
        super(message);
    }

    public PasswordMismatchException(ExceptionMessage message) {
        super(message.getMessage());
    }
}
