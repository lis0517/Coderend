package com.sparta.fifteen.repository;

import com.sparta.fifteen.entity.LogoutAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LogoutAccessTokenRepository extends JpaRepository<LogoutAccessToken, Long> {
    Optional<LogoutAccessToken> findByUsername(String username);

    boolean existsByToken(String token);

    void deleteByUsername(String username);
}
