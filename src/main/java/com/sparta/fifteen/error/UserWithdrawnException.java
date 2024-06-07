package com.sparta.fifteen.error;

public class UserWithdrawnException extends RuntimeException {
    public UserWithdrawnException(String message) {
        super(message);
    }
}
