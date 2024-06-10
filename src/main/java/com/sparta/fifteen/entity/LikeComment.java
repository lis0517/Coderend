package com.sparta.fifteen.entity;

import com.sparta.fifteen.repository.CommentRepository;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class LikeComment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;


    @Column(nullable = false)
    private long userId;

    @Column(nullable = false)
    private long commentId;

    public LikeComment(long userId, long commentId) {
        this.userId = userId;
        this.commentId = commentId;
    }
}


