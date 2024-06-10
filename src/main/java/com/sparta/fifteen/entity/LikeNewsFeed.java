package com.sparta.fifteen.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Table(name = "like_newsfeed")
@Setter
@NoArgsConstructor
public class LikeNewsFeed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "likeN_id")
    private Long id;

    @Column(nullable = false)
    private long userId;

    @Column(nullable = false)
    private long newsfeedId;

    public LikeNewsFeed(long userId, long newsfeedId) {
        this.userId = userId;
        this.newsfeedId = newsfeedId;
    }
}
