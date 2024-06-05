package com.sparta.fifteen.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class UserRefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    private String refreshToken;

    public UserRefreshToken(String refreshToken, User user){
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

    public Boolean validRefreshToken(String refreshToken){
        return this.refreshToken.equals(refreshToken);
    }

    public void setUser(User user){
        this.user = user;
    }
}
