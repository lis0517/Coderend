package com.sparta.fifteen.service;

import com.sparta.fifteen.dto.CommentRequestDto;
import com.sparta.fifteen.dto.CommentResponseDto;
import com.sparta.fifteen.entity.Comment;
import com.sparta.fifteen.entity.NewsFeed;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.repository.CommentRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public CommentResponseDto createComment(User user, NewsFeed newsFeed, CommentRequestDto commentRequestDto) {
        Comment comment = new Comment(commentRequestDto.getComment(), user, newsFeed);

        commentRepository.save(comment);

        return CommentResponseDto.toDto(comment);
    }
}
