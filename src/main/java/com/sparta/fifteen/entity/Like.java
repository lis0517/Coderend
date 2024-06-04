package com.sparta.fifteen.entity;

import jakarta.persistence.*;

@Entity
public class Like extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "content_id")
    private Long contentId;

    @Column(name="content_type")
    @Enumerated(EnumType.STRING)
    private ContentTypeEnum contentType;

    public Like(Long userId, Long contentId, ContentTypeEnum contentType) {
        this.userId = userId;
        this.contentId = contentId;
        this.contentType = contentType;
    }
}
