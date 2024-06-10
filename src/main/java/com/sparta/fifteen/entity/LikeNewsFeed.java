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

    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "newsfeed_id", nullable = false)
    private NewsFeed newsFeed;

    public LikeNewsFeed(User user, NewsFeed newsFeed) {
        this.user = user;
        this.newsFeed = newsFeed;
    }
}
