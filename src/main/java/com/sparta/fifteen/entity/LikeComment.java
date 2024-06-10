package com.sparta.fifteen.entity;

import com.sparta.fifteen.repository.CommentRepository;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "like_comment")
@Getter
@Setter
@NoArgsConstructor
public class LikeComment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;


    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    public LikeComment(User user, Comment comment) {
        this.user = user;
        this.comment = comment;
    }
}


