package com.sparta.fifteen.entity;

import com.sparta.fifteen.dto.CommentRequestDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comment")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Comment extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String comment;
    private Long likes;

    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "news_feed_id", nullable = false)
    private NewsFeed newsfeed;

    public Comment(String comment, User user, NewsFeed newsfeed) {
        this.comment = comment;
        this.user = user;
        this.newsfeed = newsfeed;
        this.likes = 0L;
    }

    public void update(CommentRequestDto commentRequestDto) {
        this.comment = commentRequestDto.getComment();
    }

    public void updatelikes(Long num){
        this.likes += num;
    }
}
