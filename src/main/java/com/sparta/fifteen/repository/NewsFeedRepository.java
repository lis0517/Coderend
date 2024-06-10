package com.sparta.fifteen.repository;

import com.sparta.fifteen.entity.NewsFeed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface NewsFeedRepository extends JpaRepository<NewsFeed, Long> {
    Page<NewsFeed> findAllByOrderByCreatedAtDesc();
    Page<NewsFeed> findAllByOrderByLikes();
    Page<NewsFeed> findByCreatedAtBetween(Date startDate, Date endDate, Pageable pageable);
}
