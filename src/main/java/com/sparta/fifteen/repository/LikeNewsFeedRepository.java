package com.sparta.fifteen.repository;

import com.sparta.fifteen.entity.LikeNewsFeed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeNewsFeedRepository extends JpaRepository<LikeNewsFeed,Long> {
    Optional<LikeNewsFeed> findByUserIdAndNewsfeedId(Long userId, Long contentId);
}
