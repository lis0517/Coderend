package com.sparta.fifteen.service;

import com.sparta.fifteen.entity.*;
import com.sparta.fifteen.error.PostNotFoundException;
import com.sparta.fifteen.error.SelfPostLikeException;
import com.sparta.fifteen.repository.*;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class LikeService {

    private final LikeCommentRepository likeCommentRepository;
    private final LikeNewsFeedRepository likeNewsFeedRepository;
    private final CommentRepository commentRepository;
    private final NewsFeedRepository newsFeedRepository;


    public LikeService(LikeCommentRepository likeCommentRepository, LikeNewsFeedRepository likeNewsFeedRepository, CommentRepository commentRepository, NewsFeedRepository newsFeedRepository) {
        this.likeCommentRepository = likeCommentRepository;
        this.likeNewsFeedRepository = likeNewsFeedRepository;
        this.commentRepository = commentRepository;
        this.newsFeedRepository = newsFeedRepository;
    }

    public void likeOrUnlike(User user, Long contentId, ContentTypeEnum contentType) {

        Long userId = user.getId();

        if(checkOwnContent(userId, contentId, contentType)) {
            throw new SelfPostLikeException("작성자가 좋아요를 할 수 없습니다.");
        }
        //자기가 만든거 일 때는 종료/예외처리
        if(contentType == ContentTypeEnum.NEWSFEED_TYPE) {

            Optional<NewsFeed> newsFeed = newsFeedRepository.findById(contentId);
            if(newsFeed.isEmpty()) {
                throw new PostNotFoundException("선택된 뉴스피드는 존재하지 않습니다."); // 예외 처리로 수정
            }
            Optional<LikeNewsFeed> existingLike = likeNewsFeedRepository.findByUserIdAndNewsfeedId(userId, contentId);
            if (existingLike.isPresent()) {
                likeNewsFeedRepository.delete(existingLike.get());
            } else {
                LikeNewsFeed like = new LikeNewsFeed( user,  newsFeed.get());
                likeNewsFeedRepository.save(like);
            }
        }else{
            Optional<Comment> comment = commentRepository.findById(contentId);
            if(comment.isEmpty()) {
                throw new PostNotFoundException("선택된 댓글은 존재하지 않습니다."); // 예외 처리로 수정
            }

            Optional<LikeComment> existingLike = likeCommentRepository.findByUserIdAndCommentId(userId, contentId);
            if (existingLike.isPresent()) {
                likeCommentRepository.delete(existingLike.get());
            } else {
                LikeComment like = new LikeComment( user,  comment.get());
                likeCommentRepository.save(like);
            }
        }



    }

    private boolean checkOwnContent(Long userId, Long contentId, ContentTypeEnum contentType) {
        //.내부 정보 확인해서 본인이 만든 게시물인지 확인
        if(contentType == ContentTypeEnum.NEWSFEED_TYPE) {
            Optional<NewsFeed> newsFeed = newsFeedRepository.findById(contentId);
            if (newsFeed.get().getAuthorId()==userId) {
                return true;
            }
        }else{
            Optional<Comment> comment = commentRepository.findById(contentId);
            if (Objects.equals(comment.get().getUser().getId(), userId)) {
                return true;
            }
        }
        return false;
    }

}
