package com.sparta.fifteen.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "comment")
@Setter
@Getter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    //뉴스피드 entity 생기면 뉴스피드 id 가져와야 함!

    public Comment(String comment, User user) {
        this.comment = comment;
        this.user = user;
    }
}
