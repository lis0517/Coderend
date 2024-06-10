package com.sparta.fifteen.error;

public class NewsFeedViewErrorException extends Throwable {
    public NewsFeedViewErrorException(Exception e) {
        super(e);
    }
    public NewsFeedViewErrorException(String message) {
        super(message);
    }
}
