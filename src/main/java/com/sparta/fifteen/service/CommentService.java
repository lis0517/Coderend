package com.sparta.fifteen.service;

import com.sparta.fifteen.dto.CommentRequestDto;
import com.sparta.fifteen.dto.CommentResponseDto;
import com.sparta.fifteen.entity.Comment;
import com.sparta.fifteen.entity.NewsFeed;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.error.CommentNotFoundException;
import com.sparta.fifteen.error.UserMismatchException;
import com.sparta.fifteen.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public List<CommentResponseDto> getComments(NewsFeed newsFeed) {
        List<Comment> commentList = commentRepository.findAllByNewsfeedId(newsFeed.getId());
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

        for (Comment comment : commentList) {
            commentResponseDtoList.add(CommentResponseDto.toDto(comment));
        }

        return commentResponseDtoList;
    }

    @Transactional
    public CommentResponseDto updateComment(User user, Long commentId, CommentRequestDto commentRequestDto) {
        Comment newComment = checkUser(commentId, user);

        newComment.update(commentRequestDto);

        return CommentResponseDto.toDto(newComment);
    }

    public void deleteComment(User user, Long commentId) {
        Comment comment = checkUser(commentId, user);

        commentRepository.delete(comment);
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("선택한 댓글이 존재하지 않습니다."));
    }

    private Comment checkUser(Long id, User user) {
        Comment comment = findCommentById(id);
        if (!Objects.equals(comment.getUser().getId(), user.getId())) {
            throw new UserMismatchException("댓글의 작성자만 수행할 수 있습니다.");
        }
        return comment;
    }
}
