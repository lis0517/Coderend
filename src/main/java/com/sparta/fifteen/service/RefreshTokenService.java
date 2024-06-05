package com.sparta.fifteen.service;

import com.sparta.fifteen.entity.RefreshToken;
import com.sparta.fifteen.error.ExceptionMessage;
import com.sparta.fifteen.error.TokenNotFoundException;
import com.sparta.fifteen.repository.RefreshTokenRepository;
import com.sparta.fifteen.util.JwtTokenProvider;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository){
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken saveRefreshToken(String username, long expirationTime){
        return refreshTokenRepository.save(RefreshToken.from(
                username, JwtTokenProvider.generateRefreshToken(), expirationTime));
    }

    public RefreshToken findRefreshTokenById(String username){
        return refreshTokenRepository.findById(username).orElseThrow(() ->
                new TokenNotFoundException(ExceptionMessage.TOKEN_NOT_FOUND));
    }

    public void deleteRefreshTokenById(String username){
        refreshTokenRepository.deleteById(username);
    }

}
