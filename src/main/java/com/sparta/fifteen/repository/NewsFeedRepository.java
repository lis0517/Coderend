package com.sparta.fifteen.repository;

import com.sparta.fifteen.entity.NewsFeed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface NewsFeedRepository extends JpaRepository<NewsFeed, Long> {
    Page<NewsFeed> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<NewsFeed> findAllByOrderByLikesDesc(Pageable pageable);
    Page<NewsFeed> findByCreatedAtBetween(Date startDate, Date endDate, Pageable pageable);
}
