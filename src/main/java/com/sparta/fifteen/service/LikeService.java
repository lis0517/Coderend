package com.sparta.fifteen.service;

import com.sparta.fifteen.entity.ContentTypeEnum;
import com.sparta.fifteen.entity.Like;
import com.sparta.fifteen.repository.LikeRepository;
import com.sparta.fifteen.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    //private final UserRepository userRepository;

    public LikeService(LikeRepository likeRepository, UserRepository userRepository) {
        //this.userRepository = userRepository;
        this.likeRepository = likeRepository;
    }

    public void likeOrUnlike(Long userId, Long contentId, ContentTypeEnum contentType) {

        //userId별 contetType별 ContentId 가 존재하는지 여부에 대한 check가 필요
        //Repositrory comment newsfeed 둘다 필요

        //자기가 만든거 일 때는 종료/예외처리
        if(checkOwnContent(userId, contentId, contentType)) {
            return;
        }

        Optional<Like> existingLike = likeRepository.findByUserIdAndContentIdAndContentType(userId, contentId, contentType);

        if (existingLike.isPresent()) {
            // 사용자가 이미 해당 콘텐츠에 좋아요를 남겼으면 좋아요 취소
            likeRepository.delete(existingLike.get());
        } else {
            // 새로운 좋아요 생성
            Like like = new Like( userId,  contentId, contentType);
            likeRepository.save(like);
        }
    }

    private boolean checkOwnContent(Long userId, Long contentId, ContentTypeEnum contentType) {
        //.내부 정보 확인해서 본인이 만든 게시물인지 확인

        return true;
    }

}
