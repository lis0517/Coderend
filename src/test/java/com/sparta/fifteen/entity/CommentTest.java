package com.sparta.fifteen.entity;

import com.sparta.fifteen.dto.CommentRequestDto;
import com.sparta.fifteen.dto.NewsFeedRequestDto;
import com.sparta.fifteen.dto.UserRegisterRequestDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    void createComment(){
        // given
        NewsFeedRequestDto requestDto = NewsFeedRequestDto
                .builder()
                .authorId(1L)
                .content("Test news feed")
                .build();

        UserRegisterRequestDto userRegisterRequestDto = UserRegisterRequestDto
                .builder()
                .username("window1234")
                .password("password1234")
                .name("Test User")
                .oneLine("hi")
                .email("test@example.com")
                .build();

        //when
        NewsFeed newsFeed = new NewsFeed(requestDto);
        User user = new User(userRegisterRequestDto);

        Comment comment = new Comment("test comment", user, newsFeed );

        //then
        assertEquals("test comment", comment.getComment());
        assertEquals(requestDto.getAuthorId(), comment.getNewsfeed().getAuthorId());
        assertEquals(userRegisterRequestDto.getUsername(), comment.getUser().getUsername());
    }

    @Test
    void updateComment(){
        // given
        CommentRequestDto requestDto = CommentRequestDto.builder().comment("update content").build();

        Comment comment = new Comment( "test comment", null, null );

        //when
        comment.update(requestDto);

        //then
        //assertEquals("test comment", comment.getComment());
        assertEquals(requestDto.getComment(), comment.getComment());
    }

    @Test
    void updateLikes(){
        // given
        Comment comment = new Comment("test comment", null, null);

        // when
        comment.updatelikes(5L);
        comment.updatelikes(-3L);

        // then
        assertEquals(2L, comment.getLikes());
    }
}