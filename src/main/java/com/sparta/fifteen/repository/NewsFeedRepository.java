package com.sparta.fifteen.repository;

import com.sparta.fifteen.entity.NewsFeed;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsFeedRepository extends JpaRepository<NewsFeed, Long> {
    Page<NewsFeed> findAllByOrderByCreatedAtDesc();
    Page<NewsFeed> findAllByOrderByLikes();
}
