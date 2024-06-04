package com.sparta.fifteen.controller;

import com.sparta.fifteen.dto.UserRegisterResponseDto;
import com.sparta.fifteen.entity.ContentTypeEnum;
import com.sparta.fifteen.service.LikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like")
@Validated
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/{contentType}/{contentId}")
    public ResponseEntity<String> toggleLike(@RequestHeader("Authorization") String token,
                                             @PathVariable Long contentId, ContentTypeEnum contentType) {

        try {
            Long userId = getUserIdFromToken(token);
            likeService.likeOrUnlike(userId, contentId, contentType);
            return new ResponseEntity<>("좋아요 토글 성공", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("좋아요 토글 실패", HttpStatus.BAD_REQUEST);
        }
    }

    private Long getUserIdFromToken(String token) {

        return 1L;
    }

}
