package com.sparta.fifteen.service;

import com.sparta.fifteen.dto.NewsFeedRequestDto;
import com.sparta.fifteen.dto.NewsFeedResponseDto;
import com.sparta.fifteen.entity.NewsFeed;
import com.sparta.fifteen.repository.CommentRepository;
import com.sparta.fifteen.repository.NewsFeedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewsFeedService {
    private final NewsFeedRepository newsFeedRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private static final Logger logger = LoggerFactory.getLogger(NewsFeedService.class);

    public NewsFeedService(NewsFeedRepository newsFeedRepository, UserService userService, CommentRepository commentRepository) {
        this.newsFeedRepository = newsFeedRepository;
        this.userService = userService;
        this.commentRepository = commentRepository;
    }

    public NewsFeed createNewsFeed(NewsFeedRequestDto newsFeedRequestDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long authorId = userService.getUserIdByUsername(userDetails.getUsername());

        NewsFeed newsFeed = new NewsFeed();
        newsFeed.setAuthorId(authorId);
        newsFeed.setContent(newsFeedRequestDto.getContent());

        return newsFeedRepository.save(newsFeed);
    }

    public NewsFeedResponseDto getNewsFeed(long newsFeedID) {
        NewsFeed newsFeed=newsFeedRepository.findById(newsFeedID).get();
        NewsFeedResponseDto newsFeedResponseDto=new NewsFeedResponseDto(newsFeed);
        return newsFeedResponseDto;
    }

    public List<NewsFeedResponseDto> getAllNewsFeed() {
        return newsFeedRepository.findAllByOrderByCreatedAtDesc().stream().map(NewsFeedResponseDto::new).toList();
    }

    @Transactional
    public NewsFeedResponseDto updateNewsFeed(long newsFeedID, NewsFeedRequestDto newsFeedRequestDto) {
        NewsFeed newsFeed=findNewsFeedById(newsFeedID);
        if(newsFeed.getAuthorId()!=newsFeedRequestDto.getAuthorId()){
            newsFeed.setContent(newsFeedRequestDto.getContent());
            newsFeedRepository.save(newsFeed);
            return new NewsFeedResponseDto(newsFeed);
        }
        else{
            throw new IllegalArgumentException("없는 게시물입니다.");
        }
    }


    @Transactional
    public String deleteNewsFeed(long newsFeedID) {
        commentRepository.deleteAllByNewsfeedId(newsFeedID);
        NewsFeed newsFeed = newsFeedRepository.findById(newsFeedID).orElseThrow(() -> new IllegalArgumentException("NewsFeed not found with id " + newsFeedID));
        newsFeedRepository.delete(newsFeed);
        return "삭제 완료";
    }

    public NewsFeed findNewsFeedById(long newsFeedID) {
        return newsFeedRepository.findById(newsFeedID).orElseThrow(() -> new IllegalArgumentException("선택한 뉴스피드는 존재하지 않습니다."));
    }

}
