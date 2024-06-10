package com.sparta.fifteen.controller;


import com.sparta.fifteen.dto.NewsFeedRequestDto;
import com.sparta.fifteen.dto.NewsFeedResponseDto;
import com.sparta.fifteen.entity.NewsFeed;
import com.sparta.fifteen.error.NewsFeedCreateErrorException;
import com.sparta.fifteen.service.NewsFeedService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

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
    public Page<NewsFeed> getNewsFeed(@RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "size", defaultValue = "10") int size){
        return newsFeedService.getAllNewsFeed(page, size);
    }

    @GetMapping("/newsfeed/newest")
    public Page<NewsFeed> getNewsFeedByDate(@RequestParam(value="page", defaultValue = "0") int page,
                                            @RequestParam(value="size", defaultValue = "10") int size){
        return newsFeedService.getNewsFeedByDate(page, size);
    }

    @GetMapping("/newsfeed/likes")
    public Page<NewsFeed> getNewsFeedLikes(@RequestParam(value="page", defaultValue = "0") int page,
                                           @RequestParam(value="size", defaultValue = "10") int size){
        return newsFeedService.getNewsFeedByLikes(page, size);
    }

    @PutMapping("/newsfeed/{newsFeedID}")
    public NewsFeedResponseDto updateNewsFeed(@PathVariable long newsFeedID, @RequestBody NewsFeedRequestDto newsFeedRequestDto){
        return newsFeedService.updateNewsFeed(newsFeedID, newsFeedRequestDto);
    }

    @DeleteMapping("/newsfeed/{newsFeedID}")
    public String deleteNewsFeed(@PathVariable long newsFeedID){
        return newsFeedService.deleteNewsFeed(newsFeedID);
    }
}
