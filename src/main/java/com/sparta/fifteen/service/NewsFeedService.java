package com.sparta.fifteen.service;

import com.sparta.fifteen.dto.NewsFeedRequestDto;
import com.sparta.fifteen.dto.NewsFeedResponseDto;
import com.sparta.fifteen.entity.NewsFeed;
import com.sparta.fifteen.repository.NewsFeedRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewsFeedService {
    private final NewsFeedRepository newsFeedRepository;
    private final UserService userService;

    public NewsFeedService(NewsFeedRepository newsFeedRepository, UserService userService) {
        this.newsFeedRepository = newsFeedRepository;
        this.userService = userService;
    }

    public NewsFeed createNewsFeed(NewsFeedRequestDto newsFeedRequestDto) {
        // Get the current logged-in user's ID
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long authorId = userService.getUserIdByUsername(userDetails.getUsername());

        // Create a new NewsFeed entity with the authorId and content from the DTO
        NewsFeed newsFeed = new NewsFeed();
        newsFeed.setAuthorId(authorId);
        newsFeed.setContent(newsFeedRequestDto.getContent());

        // Save the entity to the repository
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

    public NewsFeedResponseDto updateNewsFeed(long newsFeedID, NewsFeedRequestDto newsFeedRequestDto) {
        NewsFeed newsFeed=newsFeedRepository.findById(newsFeedID).get();
        newsFeed.setContent(newsFeedRequestDto.getContent());
        newsFeedRepository.save(newsFeed);
        return new NewsFeedResponseDto(newsFeed);
    }

    public long deleteNewsFeed(long newsFeedID) {
        NewsFeed newsFeed=newsFeedRepository.findById(newsFeedID).get();
        newsFeedRepository.delete(newsFeed);
        return newsFeedID;
    }

    public NewsFeed findNewsFeedById(long newsFeedID) {
        return newsFeedRepository.findById(newsFeedID).orElseThrow(() -> new IllegalArgumentException("선택한 뉴스피드는 존재하지 않습니다."));
    }

}
