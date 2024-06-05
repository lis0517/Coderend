package com.sparta.fifteen.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class LikeComment extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Long conmmentId;


    public LikeComment(Long userId, Long conmmentId) {
        this.userId = userId;
        this.conmmentId = conmmentId;
    }
}
