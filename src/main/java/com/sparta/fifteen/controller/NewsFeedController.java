package com.sparta.fifteen.controller;


import com.sparta.fifteen.dto.NewsFeedRequestDto;
import com.sparta.fifteen.dto.NewsFeedResponseDto;
import com.sparta.fifteen.service.NewsFeedService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class NewsFeedController {
    private NewsFeedService newsFeedService;
    @PostMapping("/newsfeed")
    public NewsFeedResponseDto create(NewsFeedRequestDto newsFeedRequestDto){
        return newsFeedService.createNewsFeed(newsFeedRequestDto);
    }

    @GetMapping("/newsfeed/{newsFeedID}")
    public NewsFeedResponseDto readNewsFeed(@PathVariable int newsFeedID){
        return newsFeedService.getNewsFeed(newsFeedID);
    }

    @GetMapping("/newsfeed")
    public List<NewsFeedResponseDto> getNewsFeed(){
        return newsFeedService.getAllNewsFeed();
    }

    @PutMapping("/newsfeed/{newsFeedID}")
    public NewsFeedResponseDto updateNewsFeed(@PathVariable int newsFeedID, NewsFeedRequestDto newsFeedRequestDto){
        return newsFeedService.updateNewsFeed(newsFeedID, newsFeedRequestDto);
    }

    @DeleteMapping("/newsfeed/{newsFeedID}")
    public int deleteNewsFeed(@PathVariable int newsFeedID){
        return newsFeedService.deleteNewsFeed(newsFeedID);
    }
}
