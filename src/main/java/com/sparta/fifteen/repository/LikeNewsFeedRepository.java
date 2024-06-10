package com.sparta.fifteen.repository;

import com.sparta.fifteen.entity.LikeNewsFeed;
import com.sparta.fifteen.entity.NewsFeed;
import com.sparta.fifteen.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeNewsFeedRepository extends JpaRepository<LikeNewsFeed,Long> {


    Optional<LikeNewsFeed> findByNewsFeedAndUser(NewsFeed newsFeed, User user);
}
