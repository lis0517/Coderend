package com.sparta.fifteen.jwt;

import com.sparta.fifteen.config.JwtConfig;
import com.sparta.fifteen.security.UserDetailsServiceImpl;
import com.sparta.fifteen.service.token.LogoutAccessTokenService;
import com.sparta.fifteen.util.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic="Jwt 검증 및 인가")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;

    private final LogoutAccessTokenService logoutAccessTokenService;

    public JwtAuthenticationFilter(UserDetailsServiceImpl userDetailsService, LogoutAccessTokenService logoutAccessTokenService){
        this.userDetailsService = userDetailsService;
        this.logoutAccessTokenService = logoutAccessTokenService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(JwtConfig.staticHeader);
        log.info(header);
        String username = null;

        String authToken = null;

        if (header != null && header.startsWith(JwtConfig.staticTokenPrefix)){
            authToken = header.replace(JwtConfig.staticTokenPrefix,"");
            log.info(authToken);
            try {
                username = JwtTokenProvider.extractUsername(authToken);
            } catch(IllegalArgumentException e){
                log.error("Error occured while fetching Username from Token.", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
            } catch (ExpiredJwtException e){
                logger.warn("The token has expired.", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired.");
            } catch(SignatureException e){
                log.error("Authentication Failed. Username or Password not valid.");
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid username or password.");
            }
        }else{
            log.warn("Couldn't find bearer string, header will be ignored.");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if( logoutAccessTokenService.existsLogoutAccessToken(authToken)){
                // 로그아웃된 토큰인 경우 인증 거부
                log.error("User already logout.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User already logout.");
            }

            if(JwtTokenProvider.validateToken(authToken, username)){
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info("authenticated user " + username + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException{
        String authorization = request.getHeader(JwtConfig.staticHeader);
        return authorization == null; //  header의 값이 null이면 해당 Filter(토큰 검증)의 동작을 수행하지 않도록 정의
    }
}
