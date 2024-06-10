package com.sparta.fifteen.util;

import com.sparta.fifteen.config.JwtConfig;
import com.sparta.fifteen.entity.UserRoleEnum;
import com.sparta.fifteen.entity.RefreshToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

public final class JwtTokenProvider {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "RefreshToken";

    private JwtTokenProvider(){
    }

    // 토큰에서 유저 이름 추출
    public static String extractUsername(String token){
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    // 토큰에서 만료 기간 추출
    public static Date extractExpiration(String token){
        Claims claims = extractAllClaims(token);
        return claims.getExpiration();
    }

    // 토큰 만료 확인
    public static Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }

    public static Boolean validateToken(String token, String username){
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // Access Token 생성
    public static String generateAccessToken(String username){
        return Jwts.builder()
                .claim(JwtConfig.staticAuthorizationKey, UserRoleEnum.USER)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JwtConfig.staticAccessTokenExpiration))
                .signWith(getSecretKey()).compact();
    }

    // Refresh Token 생성
    // subject 사용하지않음
    public static String generateRefreshToken(){

        return Jwts.builder()
                .claim(JwtConfig.staticAuthorizationKey, UserRoleEnum.USER)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JwtConfig.staticRefreshTokenExpiration))
                .signWith(getSecretKey()).compact();
    }

    // cookie에서 refresh token 가져오기
    public static String getRefreshTokenFromRequest(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(REFRESH_TOKEN_COOKIE_NAME)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // Refresh Token 쿠키에 저장
    public static void setRefreshTokenAtCookie(RefreshToken refreshToken){
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken.getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(JwtConfig.staticRefreshTokenExpirationSecond);
        HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder
                .getRequestAttributes())).getResponse();
        assert response != null;
        response.addCookie(cookie);
    }

    // secret key 조립
    private static SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(JwtConfig.staticSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 추출
    private static Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
