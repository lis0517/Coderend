package com.sparta.fifteen.entity;

import com.sparta.fifteen.dto.NewsFeedRequestDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class NewsFeedTest {

    @Test
    void createNewsfeed(){
        //given
        NewsFeedRequestDto requestDto = NewsFeedRequestDto
                .builder()
                .authorId(1L)
                .content("Test news feed")
                .build();

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
        newsFeed.initLikes();

        //when
        newsFeed.updateLikes(5L);

        //then
        assertEquals(5L, newsFeed.getLikes());
    }
}