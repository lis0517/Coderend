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
    public NewsFeedController(NewsFeedService newsFeedService) {
        this.newsFeedService = newsFeedService;
    }
    @PostMapping("/newsfeed")
    public NewsFeed create(@RequestBody NewsFeedRequestDto newsFeedRequestDto) throws NewsFeedCreateErrorException {
        return newsFeedService.createNewsFeed(newsFeedRequestDto);
    }

    @GetMapping("/newsfeed/{newsFeedID}")
    public NewsFeedResponseDto readNewsFeed(@PathVariable long newsFeedID){
        return newsFeedService.getNewsFeed(newsFeedID);
    }

    @GetMapping("/newsfeed")
    public List<NewsFeedResponseDto> getNewsFeed(){
        return newsFeedService.getAllNewsFeed();
    }

    @PutMapping("/newsfeed/{newsFeedID}")
    public NewsFeedResponseDto updateNewsFeed(@PathVariable long newsFeedID, NewsFeedRequestDto newsFeedRequestDto){
        return newsFeedService.updateNewsFeed(newsFeedID, newsFeedRequestDto);
    }

    @DeleteMapping("/newsfeed/{newsFeedID}")
    public long deleteNewsFeed(@PathVariable long newsFeedID){
        return newsFeedService.deleteNewsFeed(newsFeedID);
    }
}
