package com.sparta.fifteen.error;

public class NewsFeedCreateErrorException extends RuntimeException {

    public NewsFeedCreateErrorException(String message) {
        super(message);
    }

    public NewsFeedCreateErrorException(ExceptionMessage message) {
        super(message.getMessage());
    }
}
