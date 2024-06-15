package com.sparta.fifteen.dto;

import jakarta.persistence.Basic;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserRegisterRequestDto {
    @Size(min = 10, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    private String username;

    @Size(min = 10)
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_])*$")
    private String password; // 로그인 Password

    private String name; // 이름

    @Email
    private String email;
    private String oneLine;

    private String statusCode;
    private String refreshToken;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Timestamp statusChangedTime;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Timestamp createdOn;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Timestamp modifiedOn;
}
