package com.sparta.fifteen.dto;

import com.sparta.fifteen.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String comment;
    private Long like;
    private Long userId;
    private Long newsFeedId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentResponseDto toDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .like(comment.getLikes())
                .userId(comment.getUser().getId())
                .newsFeedId(comment.getNewsfeed().getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getModifiedAt())
                .build();
    }
}
