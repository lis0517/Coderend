package com.sparta.fifteen.controller;

import com.sparta.fifteen.dto.CommentRequestDto;
import com.sparta.fifteen.dto.CommentResponseDto;
import com.sparta.fifteen.entity.NewsFeed;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.service.CommentService;
import com.sparta.fifteen.service.NewsFeedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/newsfeed/{newsfeedId}/comments")
public class CommentController {
    private final CommentService commentService;
    private final NewsFeedService newsFeedService;

    public CommentController(CommentService commentService, NewsFeedService newsFeedService) {
        this.commentService = commentService;
        this.newsFeedService = newsFeedService;
    }

    @GetMapping("")
    public ResponseEntity<String> createComment(@PathVariable("newsfeedId") Long newsFeedId, @RequestBody CommentRequestDto commentRequestDto) {
        NewsFeed newsFeed = newsFeedService.findNewsFeedById(newsFeedId);

        return ResponseEntity.status(HttpStatus.CREATED).body("성공했습니다.");
    }
}
