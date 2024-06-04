package com.sparta.fifteen.controller;

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
    public ResponseEntity<String> toggleLike(@RequestHeader("userId") Long userId,
                                             @PathVariable Long contentId,
                                             @PathVariable ContentTypeEnum contentType) {
        likeService.likeOrUnlike(userId, contentId, contentType);
        return new ResponseEntity<>("좋아요 토글 성공", HttpStatus.OK);
    }


}
