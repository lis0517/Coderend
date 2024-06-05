package com.sparta.fifteen.repository;

import com.sparta.fifteen.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    // 로그인 시 사용예정
    Optional<User> findByUsername(String username);

    User findByUserId(Long userId);
}
