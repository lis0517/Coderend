package com.sparta.fifteen.repository.token;

import com.sparta.fifteen.entity.token.RefreshToken;
import com.sparta.fifteen.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    boolean existsByUser(User user);
}
