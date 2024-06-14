package com.sparta.fifteen.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest {

    @Test
    void testUpdateRefreshToken() {
        // given
        RefreshToken refreshToken = new RefreshToken();
        String newToken = "new-token";

        // when
        refreshToken.updateRefreshToken(newToken);

        // then
        assertEquals(newToken, refreshToken.getToken());
    }

    @Test
    void testUpdateExpirationDate() {
        // given
        RefreshToken refreshToken = new RefreshToken();
        LocalDateTime newExpirationDate = LocalDateTime.now().plusDays(7);

        // when
        refreshToken.updateExpirationDate(newExpirationDate);

        // then
        assertEquals(newExpirationDate, refreshToken.getExpirationDate());
    }
}