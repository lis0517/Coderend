package com.sparta.fifteen.controller;

import com.sparta.fifteen.dto.CommentRequestDto;
import com.sparta.fifteen.dto.CommentResponseDto;
import com.sparta.fifteen.entity.NewsFeed;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.security.UserDetailsImpl;
import com.sparta.fifteen.service.CommentService;
import com.sparta.fifteen.service.NewsFeedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/newsfeeds/{newsfeedId}/comments")
public class CommentController {
    private final CommentService commentService;
    private final NewsFeedService newsFeedService;

    public CommentController(CommentService commentService, NewsFeedService newsFeedService) {
        this.commentService = commentService;
        this.newsFeedService = newsFeedService;
    }

    @PostMapping("")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable("newsfeedId") Long newsFeedId, @RequestBody CommentRequestDto commentRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        NewsFeed newsFeed = newsFeedService.findNewsFeedById(newsFeedId);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(user, newsFeed, commentRequestDto));
    }

    @GetMapping("")
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable("newsfeedId") Long newsFeedId) {
        NewsFeed newsFeed = newsFeedService.findNewsFeedById(newsFeedId);

        return ResponseEntity.ok().body(commentService.getComments(newsFeed));
    }
}
