package com.sparta.fifteen.entity;

import com.sparta.fifteen.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "logout_access_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogoutAccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private long expirationTime;
}
