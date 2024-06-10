package com.sparta.fifteen.error;

public class NewsFeedCreateErrorException extends Exception {
    public NewsFeedCreateErrorException(Exception message){
        super(message);
    }
    public NewsFeedCreateErrorException(ExceptionMessage message){
        super(message.getMessage());
    }
}
