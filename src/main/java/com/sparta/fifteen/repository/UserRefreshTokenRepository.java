package com.sparta.fifteen.repository;

import com.sparta.fifteen.entity.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {
}
