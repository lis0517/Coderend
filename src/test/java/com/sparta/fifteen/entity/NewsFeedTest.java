package com.sparta.fifteen.entity;

import com.sparta.fifteen.dto.NewsFeedRequestDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class NewsFeedTest {

    @Test
    void createNewsfeed(){
        //given
        NewsFeedRequestDto requestDto = new NewsFeedRequestDto();
        requestDto.setAuthorId(1L);
        requestDto.setContent("Test news feed");

        //when
        NewsFeed newsFeed = new NewsFeed(requestDto);

        //then
        assertEquals(requestDto.getAuthorId(),  newsFeed.getAuthorId());
        assertEquals(requestDto.getContent(), newsFeed.getContent());
    }

    @Test
    void updateLikes(){
        //given
        NewsFeed newsFeed = new NewsFeed();
        newsFeed.setLikes(0L);

        //when
        newsFeed.updateLikes(5L);

        //then
        assertEquals(5L, newsFeed.getLikes());
    }
}