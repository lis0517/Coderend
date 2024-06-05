package com.sparta.fifteen.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor
public class Comment extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String comment;
    @Column(columnDefinition = "bigint default 0")
    private Long like;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //뉴스피드 entity 생기면 뉴스피드 id 가져와야 함!
    @ManyToOne
    @JoinColumn(name = "newsfeed_id", nullable = false)
    private NewsFeed newsfeed;

    public Comment(String comment, User user, NewsFeed newsfeed) {
        this.comment = comment;
        this.user = user;
        this.newsfeed = newsfeed;
    }
}
