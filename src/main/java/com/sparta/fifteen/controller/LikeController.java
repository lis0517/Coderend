package com.sparta.fifteen.controller;

import com.sparta.fifteen.dto.UserRegisterResponseDto;
import com.sparta.fifteen.entity.ContentTypeEnum;
import com.sparta.fifteen.service.LikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Validated
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/newsfeed/{newsfeedId}/likeToggle")
    public ResponseEntity<String> toggleLikeNewsFeed(@RequestHeader("Authorization") String token,
                                             @PathVariable Long newsfeedId) {

        try {
            Long userId = getUserIdFromToken(token);
            likeService.likeOrUnlike(userId, newsfeedId, ContentTypeEnum.NEWSFEED_TYPE);
            return new ResponseEntity<>("좋아요 토글 성공", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("좋아요 토글 실패", HttpStatus.BAD_REQUEST);
        }
    }



    @PostMapping("newsfeed/{newsfeedId}/comments/{commentId}/likeToggle")
    public ResponseEntity<String> toggleLikeComment(@RequestHeader("Authorization") String token,
                                             @PathVariable Long commentId) {

        try {
            Long userId = getUserIdFromToken(token);
            likeService.likeOrUnlike(userId, commentId, ContentTypeEnum.COMMENT_TYPE);
            return new ResponseEntity<>("좋아요 토글 성공", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("좋아요 토글 실패", HttpStatus.BAD_REQUEST);
        }
    }

    private Long getUserIdFromToken(String token) {

        return 1L;
    }

}
