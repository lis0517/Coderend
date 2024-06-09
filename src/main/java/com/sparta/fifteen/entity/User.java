package com.sparta.fifteen.entity;

import com.sparta.fifteen.dto.ProfileRequestDto;
import com.sparta.fifteen.dto.UserRegisterRequestDto;
import com.sparta.fifteen.entity.token.RefreshToken;
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
    private String username; // 로그인 ID

    @NotNull
    @Column(nullable = false, length = 100) // 단방향 인코딩된 패스워드 길이
    private String password; // 로그인 Password

    private String name; // 이름
    private String oneLine; // 한 줄 소개
    private String statusCode; // 상태 코드

    @Email
    private String email;

    @OneToOne(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private RefreshToken refreshToken;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp statusChangedTime;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdOn;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp modifiedOn;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EmailVerification emailVerification;


//    @OneToMany(mappedBy = "user")
//    private List<NewsFeed> newsFeedList = new ArrayList<>();
//
//    @OneToMany(mappedBy = "user")
//    private List<Comment> commentList = new ArrayList<>();
//
//    @OneToMany(mappedBy = "user")
//    private List<Likes> likesList = new ArrayList<>();

    public User(UserRegisterRequestDto requestDto) {
        this.username = requestDto.getUsername();
        this.password = requestDto.getPassword();
        this.name = requestDto.getName();
        this.oneLine = requestDto.getOneLine();
        this.email = requestDto.getEmail();
        this.statusCode = getStatusCode();
        this.statusChangedTime = getStatusChangedTime();
        this.createdOn = getCreatedOn();
        this.modifiedOn = getModifiedOn();
    }

    public void updateProfile(ProfileRequestDto profileRequestDto) {
        this.name = profileRequestDto.getName();
        this.password = profileRequestDto.getNewPassword();
        this.oneLine = profileRequestDto.getOneline();

        this.modifiedOn =  new Timestamp(System.currentTimeMillis());
    }
}
