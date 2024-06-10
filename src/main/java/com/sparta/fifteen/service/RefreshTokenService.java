package com.sparta.fifteen.service;

import com.sparta.fifteen.config.JwtConfig;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.entity.RefreshToken;
import com.sparta.fifteen.error.ExceptionMessage;
import com.sparta.fifteen.error.TokenNotFoundException;
import com.sparta.fifteen.repository.RefreshTokenRepository;
import com.sparta.fifteen.util.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository){
        this.refreshTokenRepository = refreshTokenRepository;
    }

    private RefreshToken createRefreshToken(User user){

        String token = JwtTokenProvider.generateRefreshToken();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpirationDate(LocalDateTime.now().plusSeconds(JwtConfig.staticRefreshTokenExpirationSecond));

        log.info("createRefreshToken: " + refreshToken.getToken());

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken updateRefreshToken(User user){
        if(refreshTokenRepository.existsByUser(user)){
            RefreshToken refreshToken = refreshTokenRepository.findByUser(user).get();
            log.info("updateRefreshToken: " + refreshToken.getToken());
            String token = JwtTokenProvider.generateRefreshToken();
            refreshToken.updateRefreshToken(token);
            refreshToken.updateExpirationDate(LocalDateTime.now().plusSeconds(JwtConfig.staticRefreshTokenExpirationSecond));
            return refreshTokenRepository.save(refreshToken);
        }else{
            return createRefreshToken(user);
        }
    }

    public User findUserByToken(String token){

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException(ExceptionMessage.TOKEN_NOT_FOUND));

        return refreshToken.getUser();
    }

    @Transactional
    public void deleteByUser(User user){
        RefreshToken refreshToken = user.getRefreshToken();
        if (refreshToken != null){
            user.setRefreshToken(null);
            refreshTokenRepository.delete(refreshToken);
        }
    }
}
