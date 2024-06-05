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
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;
    private final NewsFeedService newsFeedService;

    public CommentController(CommentService commentService, NewsFeedService newsFeedService) {
        this.commentService = commentService;
        this.newsFeedService = newsFeedService;
    }

    @PostMapping("{/{newsfeedId}")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable("newsfeedId") Long newsFeedId, @RequestBody CommentRequestDto commentRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        NewsFeed newsFeed = newsFeedService.findNewsFeedById(newsFeedId);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(user, newsFeed, commentRequestDto));
    }

    @GetMapping("/{newsfeedId}")
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable("newsfeedId") Long newsFeedId) {
        NewsFeed newsFeed = newsFeedService.findNewsFeedById(newsFeedId);

        return ResponseEntity.ok().body(commentService.getComments(newsFeed));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment( @PathVariable("commentId") Long commentId, @RequestBody CommentRequestDto commentRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        return ResponseEntity.ok().body(commentService.updateComment(user, commentId, commentRequestDto));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable("commentId") Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        commentService.deleteComment(user, commentId);

        return ResponseEntity.ok().body("댓글 삭제에 성공했습니다.");
    }
}
