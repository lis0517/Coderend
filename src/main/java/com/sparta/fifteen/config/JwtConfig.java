package com.sparta.fifteen.config;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    @Value("${jwt.authorization.key}")
    private String AUTHORIZATION_KEY;

    @Value("${access-token-expiration}")
    private Long ACCESS_TOKEN_EXPIRATION;

    @Value("${refresh-token-expiration}")
    private Long REFRESH_TOKEN_EXPIRATION;

    @Value("${jwt.header.string}")
    private String HEADER_STRING;
    @Value("${jwt.token.prefix}")
    private String TOKEN_PREFIX;

    public static String staticSecretKey;
    public static String staticAuthorizationKey;
    public static String staticHeader;
    public static String staticTokenPrefix;

    public static long staticAccessTokenExpiration; // access token 만료 시간
    public static long staticRefreshTokenExpiration; // refresh token 만료 시간

    @PostConstruct
    public void init(){
        staticSecretKey = SECRET_KEY;
        staticAuthorizationKey = AUTHORIZATION_KEY;
        staticHeader = HEADER_STRING;
        staticTokenPrefix = TOKEN_PREFIX;
        staticAccessTokenExpiration = ACCESS_TOKEN_EXPIRATION; // test : 1분
        staticRefreshTokenExpiration = REFRESH_TOKEN_EXPIRATION; // test : 5분
    }
}
