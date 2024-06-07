package com.sparta.fifteen.error;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(String message) {
        super(message);
    }

    public CommentNotFoundException() {
        super("Comment Not Found Exception");
    }
}
