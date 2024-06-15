package com.sparta.fifteen.service;

import com.sparta.fifteen.dto.NewsFeedRequestDto;
import com.sparta.fifteen.dto.NewsFeedResponseDto;
import com.sparta.fifteen.entity.NewsFeed;
import com.sparta.fifteen.repository.CommentRepository;
import com.sparta.fifteen.repository.NewsFeedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

        NewsFeed newsFeed = NewsFeed
                .builder()
                .authorId(authorId)
                .content(newsFeedRequestDto.getContent())
                .build();

        return newsFeedRepository.save(newsFeed);
    }

    public NewsFeedResponseDto getNewsFeed(long newsFeedID) {
        NewsFeed newsFeed=newsFeedRepository.findById(newsFeedID).get();
        NewsFeedResponseDto newsFeedResponseDto = new NewsFeedResponseDto(newsFeed);
        return newsFeedResponseDto;
    }

    public Page<NewsFeed> getAllNewsFeed(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return newsFeedRepository.findAll(pageable);
    }

    public Page<NewsFeed> getNewsFeedByDate(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return newsFeedRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<NewsFeed> getNewsFeedByLikes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return newsFeedRepository.findAllByOrderByLikesDesc(pageable);
    }

    public Page<NewsFeed> searchNewsFeed(int page, int size, Date startingDate, Date endingDate) {
        Pageable pageable = PageRequest.of(page, size);
        return newsFeedRepository.findByCreatedAtBetween(startingDate, endingDate, pageable);
    }

    @Transactional
    public NewsFeedResponseDto updateNewsFeed(long newsFeedID, NewsFeedRequestDto newsFeedRequestDto) {
        NewsFeed newsFeed=findNewsFeedById(newsFeedID);
        if(newsFeed.getAuthorId()!=newsFeedRequestDto.getAuthorId()){
            newsFeed.updateContent(newsFeedRequestDto.getContent());
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
