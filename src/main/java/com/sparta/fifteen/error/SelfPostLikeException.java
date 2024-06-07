package com.sparta.fifteen.error;

public class SelfPostLikeException extends RuntimeException {

    public SelfPostLikeException(String message) {
        super(message);
    }

    public SelfPostLikeException(ExceptionMessage message) {
        super(message.getMessage());
    }
}
