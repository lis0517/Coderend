package com.sparta.fifteen.service;

import com.sparta.fifteen.dto.CommentRequestDto;
import com.sparta.fifteen.dto.CommentResponseDto;
import com.sparta.fifteen.entity.Comment;
import com.sparta.fifteen.entity.NewsFeed;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.error.CommentNotFoundException;
import com.sparta.fifteen.error.UserMismatchException;
import com.sparta.fifteen.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Test
    @DisplayName("댓글 생성")
    void createComment_Success() {
        // given
        User user = User.builder().id(1L).build();
        NewsFeed newsFeed = NewsFeed.builder().id(1L).build();
        CommentRequestDto requestDto = CommentRequestDto.builder().comment("update content").build();

        // when
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CommentResponseDto responseDto = commentService.createComment(user, newsFeed, requestDto);

        // then
        assertNotNull(responseDto);
        assertEquals(requestDto.getComment(), responseDto.getComment());
        assertEquals(user.getId(), responseDto.getUserId());
        assertEquals(newsFeed.getId(), responseDto.getNewsFeedId());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("뉴스피드의 댓글 목록 조회")
    void getComments_Success() {
        // given
        User user = User.builder().id(1L).build();
        NewsFeed newsFeed = NewsFeed.builder().id(1L).build();
        List<Comment> commentList = new ArrayList<>();
        commentList.add(Comment.builder().id(1L).comment("Comment 1").user(user).newsfeed(newsFeed).build());
        commentList.add(Comment.builder().id(2L).comment("Comment 2").user(user).newsfeed(newsFeed).build());

        // when
        when(commentRepository.findAllByNewsfeedId(newsFeed.getId())).thenReturn(commentList);
        List<CommentResponseDto> responseDtoList = commentService.getComments(newsFeed);

        // then
        assertNotNull(responseDtoList);
        assertEquals(commentList.size(), responseDtoList.size());
        for (int i = 0; i < commentList.size(); i++) {
            assertEquals(commentList.get(i).getComment(), responseDtoList.get(i).getComment());
        }
        verify(commentRepository, times(1)).findAllByNewsfeedId(newsFeed.getId());
    }

    @Test
    @DisplayName("댓글 수정")
    void updateComment_Success() {
        // given
        User user = User.builder().id(1L).build();
        NewsFeed newsFeed = NewsFeed.builder().id(1L).build();
        Comment comment = Comment.builder().id(1L).user(user).newsfeed(newsFeed).comment("Old comment").build();
        CommentRequestDto requestDto = CommentRequestDto.builder().comment("update content").build();

        // when
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        CommentResponseDto responseDto = commentService.updateComment(user, comment.getId(), requestDto);

        // then
        assertNotNull(responseDto);
        assertEquals(requestDto.getComment(), responseDto.getComment());
        verify(commentRepository, times(1)).findById(comment.getId());
    }

    @Test
    @DisplayName("댓글 수정 - 작성자 불일치")
    void updateComment_UserMismatch() {
        // given
        User user = User.builder().id(1L).build();
        User anotherUser = User.builder().id(2L).build();
        Comment comment = Comment.builder().id(1L).user(anotherUser).build();
        CommentRequestDto requestDto = CommentRequestDto.builder().comment("update content").build();

        // when
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        // then
        assertThrows(UserMismatchException.class, () -> commentService.updateComment(user, comment.getId(), requestDto));
        verify(commentRepository, times(1)).findById(comment.getId());
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteComment_Success() {
        // given
        User user = User.builder().id(1L).build();
        Comment comment = Comment.builder().id(1L).user(user).build();

        // when
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        commentService.deleteComment(user, comment.getId());

        // then
        verify(commentRepository, times(1)).findById(comment.getId()); // find id가 한번 호출되었는지 검사
        verify(commentRepository, times(1)).delete(comment); // delete가 한번 호출되었는지
    }

    @Test
    @DisplayName("댓글 삭제 - 작성자 불일치")
    void deleteComment_UserMismatch() {
        // given
        User user = User.builder().id(1L).build();
        User anotherUser = User.builder().id(2L).build();
        Comment comment = Comment.builder().id(1L).user(anotherUser).build();

        // when
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        // then
        assertThrows(UserMismatchException.class, () -> commentService.deleteComment(user, comment.getId())); // UserMismatchException 발생했는지 확인
        verify(commentRepository, times(1)).findById(comment.getId()); // find id가 한번 호출되었는지 검사
        verify(commentRepository, never()).delete(any(Comment.class)); // delete가 호출되지않았는지 확인
    }
}