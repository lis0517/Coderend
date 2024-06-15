package com.sparta.fifteen.service;

import com.sparta.fifteen.entity.*;
import com.sparta.fifteen.error.CommentNotFoundException;
import com.sparta.fifteen.error.PostNotFoundException;
import com.sparta.fifteen.error.SelfPostLikeException;
import com.sparta.fifteen.repository.CommentRepository;
import com.sparta.fifteen.repository.LikeCommentRepository;
import com.sparta.fifteen.repository.LikeNewsFeedRepository;
import com.sparta.fifteen.repository.NewsFeedRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeCommentRepository likeCommentRepository;

    @Mock
    private LikeNewsFeedRepository likeNewsFeedRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private NewsFeedRepository newsFeedRepository;

    @Test
    @DisplayName("뉴스피드 좋아요 - 좋아요 없음")
    void likeOrUnlike_NewsFeed_NoExistingLike() {
        // given
        User user = User.builder().id(1L).build();
        NewsFeed newsFeed = NewsFeed.builder().id(1L).likes(0L).authorId(2L).build();
        when(newsFeedRepository.findById(newsFeed.getId())).thenReturn(Optional.of(newsFeed));
        when(likeNewsFeedRepository.findByNewsFeedAndUser(newsFeed, user)).thenReturn(Optional.empty());

        // when
        likeService.likeOrUnlike(user, newsFeed.getId(), ContentTypeEnum.NEWSFEED_TYPE);

        // then
        verify(likeNewsFeedRepository, times(1)).save(any(LikeNewsFeed.class));
        assertEquals(1L, newsFeed.getLikes());
    }

    @Test
    @DisplayName("뉴스피드 좋아요 - 좋아요 있음")
    void likeOrUnlike_NewsFeed_ExistingLike() {
        // given
        User user = User.builder().id(1L).build();
        NewsFeed newsFeed = NewsFeed.builder().id(1L).likes(0L).authorId(2L).build();
        LikeNewsFeed existingLike = new LikeNewsFeed(user, newsFeed);
        when(newsFeedRepository.findById(newsFeed.getId())).thenReturn(Optional.of(newsFeed));
        when(likeNewsFeedRepository.findByNewsFeedAndUser(newsFeed, user)).thenReturn(Optional.of(existingLike));

        // when
        likeService.likeOrUnlike(user, newsFeed.getId(), ContentTypeEnum.NEWSFEED_TYPE);

        // then
        verify(likeNewsFeedRepository, times(1)).delete(existingLike);
        assertEquals(1L, newsFeed.getLikes());
    }

    @Test
    @DisplayName("뉴스피드 좋아요 - 작성자 본인")
    void likeOrUnlike_NewsFeed_OwnPost() {
        // given
        User user = User.builder().id(1L).build();
        NewsFeed newsFeed = NewsFeed.builder().id(1L).authorId(1L).build();
        when(newsFeedRepository.findById(newsFeed.getId())).thenReturn(Optional.of(newsFeed));

        // when, then
        assertThrows(SelfPostLikeException.class, () -> likeService.likeOrUnlike(user, newsFeed.getId(), ContentTypeEnum.NEWSFEED_TYPE));
    }

    @Test
    @DisplayName("뉴스피드 좋아요 - 존재하지 않는 뉴스피드")
    void likeOrUnlike_NewsFeed_PostNotFound() {
        // given
        User user = User.builder().id(1L).build();
        Long newsFeedId = 1L;
        when(newsFeedRepository.findById(newsFeedId)).thenReturn(Optional.empty());

        // when, then
        assertThrows(NoSuchElementException.class, () -> likeService.likeOrUnlike(user, newsFeedId, ContentTypeEnum.NEWSFEED_TYPE));
    }

    @Test
    @DisplayName("댓글 좋아요 - 좋아요 없음")
    void likeOrUnlike_Comment_NoExistingLike() {
        // given
        User user = User.builder().id(1L).build();
        Comment comment = Comment.builder().id(1L).likes(0L).user(User.builder().id(2L).build()).build();
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(likeCommentRepository.findByUserIdAndCommentId(user.getId(), comment.getId())).thenReturn(Optional.empty());

        // when
        likeService.likeOrUnlike(user, comment.getId(), ContentTypeEnum.COMMENT_TYPE);

        // then
        verify(likeCommentRepository, times(1)).save(any(LikeComment.class));
        assertEquals(1L, comment.getLikes());
    }

    @Test
    @DisplayName("댓글 좋아요 - 좋아요 있음")
    void likeOrUnlike_Comment_ExistingLike() {
        // given
        User user = User.builder().id(1L).build();
        Comment comment = Comment.builder().id(1L).likes(0L).user(User.builder().id(2L).build()).build();
        LikeComment existingLike = new LikeComment(user, comment);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(likeCommentRepository.findByUserIdAndCommentId(user.getId(), comment.getId())).thenReturn(Optional.of(existingLike));

        // when
        likeService.likeOrUnlike(user, comment.getId(), ContentTypeEnum.COMMENT_TYPE);

        // then
        verify(likeCommentRepository, times(1)).delete(existingLike);
        assertEquals(-1L, comment.getLikes());
    }

    @Test
    @DisplayName("댓글 좋아요 - 작성자 본인")
    void likeOrUnlike_Comment_OwnComment() {
        // given
        User user = User.builder().id(1L).build();
        Comment comment = Comment.builder().id(1L).user(user).build();
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        // when, then
        assertThrows(SelfPostLikeException.class, () -> likeService.likeOrUnlike(user, comment.getId(), ContentTypeEnum.COMMENT_TYPE));
    }

    @Test
    @DisplayName("댓글 좋아요 - 존재하지 않는 댓글")
    void likeOrUnlike_Comment_CommentNotFound() {
        // given
        User user = User.builder().id(1L).build();
        Long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // when, then
        assertThrows(NoSuchElementException.class, () -> likeService.likeOrUnlike(user, commentId, ContentTypeEnum.COMMENT_TYPE));
    }
}