package com.sparta.fifteen.entity;

public enum UserStatusEnum {
    NORMAL("정상"),
    PENDING("인증대기"),
    WITHDRAWN("탈퇴");

    private final String status;

    UserStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
