package com.sparta.fifteen.service.token;

import com.sparta.fifteen.config.JwtConfig;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.entity.token.RefreshToken;
import com.sparta.fifteen.error.ExceptionMessage;
import com.sparta.fifteen.error.TokenNotFoundException;
import com.sparta.fifteen.repository.token.RefreshTokenRepository;
import com.sparta.fifteen.util.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@Slf4j
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository){
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(User user){

        String token = JwtTokenProvider.generateRefreshToken();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpirationDate(LocalDateTime.now().plusSeconds(JwtConfig.staticRefreshTokenExpirationSecond));

        log.info("createRefreshToken" + refreshToken.getToken());

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken updateRefreshToken(User user){
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseGet(() -> createRefreshToken(user));
        log.info("updateRefreshToken" + refreshToken.getToken());


        String token = JwtTokenProvider.generateRefreshToken();

        refreshToken.updateRefreshToken(token);
        refreshToken.updateExpirationDate(LocalDateTime.now().plusSeconds(JwtConfig.staticRefreshTokenExpirationSecond));
        return refreshTokenRepository.save(refreshToken);
    }

    public User findUserByToken(String token){

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException(ExceptionMessage.TOKEN_NOT_FOUND));

        return refreshToken.getUser();
    }

    @Transactional
    public void deleteByUser(User user){
        refreshTokenRepository.deleteByUser(user);
    }
}
