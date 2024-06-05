package com.sparta.fifteen;

import com.sparta.fifteen.util.JwtTokenProvider;
import jakarta.persistence.PrePersist;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtTokenTest {

    @Test
    public void generateToken(){
        String accessToken = JwtTokenProvider.generateAccessToken("user123");
        String refreshToken = JwtTokenProvider.generateRefreshToken();

        System.out.println(
                accessToken + ", " +
                JwtTokenProvider.extractUsername(accessToken) + ", "
                + JwtTokenProvider.extractExpiration(accessToken));

        System.out.println(
                refreshToken + ", " +
                JwtTokenProvider.extractUsername(refreshToken) + ", "
                + JwtTokenProvider.extractExpiration(refreshToken));
    }

}
