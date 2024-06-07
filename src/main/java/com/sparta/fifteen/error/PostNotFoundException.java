package com.sparta.fifteen.error;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message) {
        super(message);
    }

    public PostNotFoundException() {
        super("Post Not Found Exception");
    }
}
