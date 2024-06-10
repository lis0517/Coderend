package com.sparta.fifteen.error;

public class UserPendingException extends RuntimeException {
    public UserPendingException(String message) {
        super(message);
    }
}
