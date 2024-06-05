package com.sparta.fifteen.service;

import com.sparta.fifteen.entity.ContentTypeEnum;
import com.sparta.fifteen.entity.LikeComment;
import com.sparta.fifteen.entity.LikeNewsFeed;
import com.sparta.fifteen.repository.LikeCommentRepository;
import com.sparta.fifteen.repository.LikeNewsFeedRepository;
import com.sparta.fifteen.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeService {

    private final LikeCommentRepository likeRepository;
    private final LikeNewsFeedRepository likeNewsFeedRepository;

    public LikeService(LikeCommentRepository likeRepository, LikeNewsFeedRepository likeNewsFeedRepository) {
        this.likeRepository = likeRepository;
        this.likeNewsFeedRepository = likeNewsFeedRepository;
    }

    public void likeOrUnlike(Long userId, Long contentId, ContentTypeEnum contentType) {


        //자기가 만든거 일 때는 종료/예외처리
        if(contentType == ContentTypeEnum.NEWSFEED_TYPE) {
            if(checkOwnContent(userId, contentId, contentType)) {
                return;
            }
            Optional<LikeNewsFeed> existingLike = likeNewsFeedRepository.findByUserIdAndNewsfeedId(userId, contentId);
            if (existingLike.isPresent()) {
                likeNewsFeedRepository.delete(existingLike.get());
            } else {
                LikeNewsFeed like = new LikeNewsFeed( userId,  contentId);
                likeNewsFeedRepository.save(like);
            }
        }else{
            if(checkOwnContent(userId, contentId, contentType)) {
                return;
            }
            Optional<LikeComment> existingLike = likeRepository.findByUserIdAndCommentId(userId, contentId);
            if (existingLike.isPresent()) {
                likeRepository.delete(existingLike.get());
            } else {
                LikeComment like = new LikeComment( userId,  contentId);
                likeRepository.save(like);
            }
        }



    }

    private boolean checkOwnContent(Long userId, Long contentId, ContentTypeEnum contentType) {
        //.내부 정보 확인해서 본인이 만든 게시물인지 확인
        return true;
    }

}
