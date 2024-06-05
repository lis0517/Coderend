package com.sparta.fifteen.util;

import com.sparta.fifteen.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

public final class JwtTokenProvider {

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
        return extractExpiration(token).before(new Date());
    }

    public static Boolean validateToken(String token, String username){
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // Access Token 생성
    public static String generateAccessToken(String username){
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JwtConfig.staticAccessTokenExpiration))
                .signWith(getSecretKey()).compact();
    }

    // Refresh Token 생성
    // subject 사용하지않음
    public static String generateRefreshToken(){
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JwtConfig.staticRefreshTokenExpiration))
                .signWith(getSecretKey()).compact();
    }

    // secret key 조립
    private static SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(JwtConfig.staticSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    private static Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
