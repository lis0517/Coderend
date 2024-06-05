package com.sparta.fifteen.service;

import com.sparta.fifteen.dto.NewsFeedRequestDto;
import com.sparta.fifteen.dto.NewsFeedResponseDto;
import com.sparta.fifteen.entity.NewsFeed;
import com.sparta.fifteen.repository.NewsFeedRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewsFeedService {
    private final NewsFeedRepository newsFeedRepository;

    public NewsFeedService(NewsFeedRepository newsFeedRepository) {
        this.newsFeedRepository = newsFeedRepository;
    }

    public NewsFeedResponseDto createNewsFeed(NewsFeedRequestDto newsFeedRequestDto){
        NewsFeed newsFeed = new NewsFeed(newsFeedRequestDto);
        newsFeedRepository.save(newsFeed);
        NewsFeedResponseDto newsFeedResponseDto=new NewsFeedResponseDto(newsFeed);
        return newsFeedResponseDto;
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
