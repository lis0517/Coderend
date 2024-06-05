package com.sparta.fifteen.entity;

import jakarta.persistence.*;

public class LikeNewsFeed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "likeN_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "newsfeed_id")
    private Long newsfeedId;

    public LikeNewsFeed(Long userId, Long newsfeedId) {
        this.userId = userId;
        this.newsfeedId = newsfeedId;
    }
}
