package com.sparta.fifteen.service.token;

import com.sparta.fifteen.entity.token.LogoutAccessToken;
import com.sparta.fifteen.repository.token.LogoutAccessTokenRepository;
import com.sparta.fifteen.util.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
public class LogoutAccessTokenService {

    private final LogoutAccessTokenRepository logoutAccessTokenRepository;

    public LogoutAccessTokenService(LogoutAccessTokenRepository logoutAccessTokenRepository){
        this.logoutAccessTokenRepository = logoutAccessTokenRepository;
    }

    public Boolean existsLogoutAccessToken(String token){
        return logoutAccessTokenRepository.existsByToken(token);
    }

    public void saveLogoutAccessToken(String accessToken, String username){
        long expirationTime = JwtTokenProvider.extractExpiration(accessToken).getTime();

        LogoutAccessToken logoutAccessToken = logoutAccessTokenRepository.findByUsername(username)
                .orElse(new LogoutAccessToken());

        logoutAccessToken.setUsername(username);
        logoutAccessToken.setToken(accessToken.trim()); // Bearer prefix 삭제 후 공백 남아있는 것이 문제 됨
        logoutAccessToken.setExpirationTime(expirationTime);

        logoutAccessTokenRepository.save(logoutAccessToken);
    }

    public void deleteByUsername(String username){
        logoutAccessTokenRepository.deleteByUsername(username);
    }
}
