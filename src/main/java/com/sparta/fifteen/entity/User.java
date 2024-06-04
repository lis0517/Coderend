package com.sparta.fifteen.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;


@Setter
@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 10, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    @Column(nullable = false, unique = true)
    private String userId; // 로그인 ID

    @NotNull
    @Column(nullable = false, length = 100) // 단방향 인코딩된 패스워드 길이
    private String password; // 로그인 Password

    private String username; // 이름
    private String oneLine; // 한 줄 소개
    private EnumType statusCode; // 상태 코드

    @Email
    private String email;

    @Column(length = 36, unique = true)
    private String refreshToken;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp statusChangedTime;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdOn;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp modifiedOn;

}
