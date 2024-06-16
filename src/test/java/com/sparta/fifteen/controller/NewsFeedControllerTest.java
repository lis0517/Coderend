package com.sparta.fifteen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.fifteen.config.TestSecurityConfig;
import com.sparta.fifteen.dto.NewsFeedRequestDto;
import com.sparta.fifteen.dto.NewsFeedResponseDto;
import com.sparta.fifteen.entity.NewsFeed;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.error.NewsFeedCreateErrorException;
import com.sparta.fifteen.security.UserDetailsImpl;
import com.sparta.fifteen.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class NewsFeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private NewsFeedService newsFeedService;

    @MockBean
    private EmailVerificationService emailVerificationService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private LikeService likeService;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private NewsFeed newsFeed;
    private NewsFeedRequestDto newsFeedRequestDto;
    private NewsFeedResponseDto newsFeedResponseDto;
    private User user;

    @BeforeEach
    void setup() {
        newsFeed = NewsFeed.builder()
                .id(1L)
                .authorId(1L)
                .content("Test NewsFeed")
                .likes(0L)
                .build();
        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .build();


        newsFeedRequestDto = NewsFeedRequestDto.builder()
                .authorId(1L)
                .content("Test NewsFeed")
                .build();

        newsFeedResponseDto = NewsFeedResponseDto.builder()
                .id(1L)
                .authorId(1L)
                .content("Test NewsFeed")
                .build();
    }

    @Test
    void create_Success() throws Exception {
        // given
        given(newsFeedService.createNewsFeed(any(NewsFeedRequestDto.class))).willReturn(newsFeed);

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/newsfeed")
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newsFeedRequestDto)));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newsFeed.getId()))
                .andExpect(jsonPath("$.authorId").value(newsFeed.getAuthorId()))
                .andExpect(jsonPath("$.content").value(newsFeed.getContent()))
                .andExpect(jsonPath("$.likes").value(newsFeed.getLikes()));
    }

    @Test
    void create_Failure() throws Exception {
        // given
        doThrow(new NewsFeedCreateErrorException("Failed to create NewsFeed"))
                .when(newsFeedService).createNewsFeed(any(NewsFeedRequestDto.class));
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/newsfeed")
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newsFeedRequestDto)));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void readNewsFeed_Success() throws Exception {
        // given
        given(newsFeedService.getNewsFeed(anyLong())).willReturn(newsFeedResponseDto);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/newsfeed/{newsFeedID}", 1L));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newsFeedResponseDto.getId()))
                .andExpect(jsonPath("$.authorId").value(newsFeedResponseDto.getAuthorId()))
                .andExpect(jsonPath("$.content").value(newsFeedResponseDto.getContent()));
    }

    @Test
    void getNewsFeed_Success() throws Exception {
        // given
        List<NewsFeed> newsFeeds = new ArrayList<>();
        newsFeeds.add(newsFeed);
        Page<NewsFeed> page = new PageImpl<>(newsFeeds);
        given(newsFeedService.getAllNewsFeed(anyInt(), anyInt())).willReturn(page);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/newsfeed")
                .param("page", "0")
                .param("size", "10"));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(newsFeed.getId()))
                .andExpect(jsonPath("$.content[0].authorId").value(newsFeed.getAuthorId()))
                .andExpect(jsonPath("$.content[0].content").value(newsFeed.getContent()))
                .andExpect(jsonPath("$.content[0].likes").value(newsFeed.getLikes()));
    }

    @Test
    void getNewsFeedByDate_Success() throws Exception {
        // given
        List<NewsFeed> newsFeeds = new ArrayList<>();
        newsFeeds.add(newsFeed);
        Page<NewsFeed> page = new PageImpl<>(newsFeeds);
        given(newsFeedService.getNewsFeedByDate(anyInt(), anyInt())).willReturn(page);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/newsfeed/newest")
                .param("page", "0")
                .param("size", "10"));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(newsFeed.getId()))
                .andExpect(jsonPath("$.content[0].authorId").value(newsFeed.getAuthorId()))
                .andExpect(jsonPath("$.content[0].content").value(newsFeed.getContent()))
                .andExpect(jsonPath("$.content[0].likes").value(newsFeed.getLikes()));
    }

    @Test
    void getNewsFeedLikes_Success() throws Exception {
        // given
        List<NewsFeed> newsFeeds = new ArrayList<>();
        newsFeeds.add(newsFeed);
        Page<NewsFeed> page = new PageImpl<>(newsFeeds);
        given(newsFeedService.getNewsFeedByLikes(anyInt(), anyInt())).willReturn(page);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/newsfeed/likes")
                .param("page", "0")
                .param("size", "10"));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(newsFeed.getId()))
                .andExpect(jsonPath("$.content[0].authorId").value(newsFeed.getAuthorId()))
                .andExpect(jsonPath("$.content[0].content").value(newsFeed.getContent()))
                .andExpect(jsonPath("$.content[0].likes").value(newsFeed.getLikes()));
    }

    @Test
    void updateNewsFeed_Success() throws Exception {
        // given
        given(newsFeedService.updateNewsFeed(anyLong(), any(NewsFeedRequestDto.class))).willReturn(newsFeedResponseDto);

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/newsfeed/{newsFeedID}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newsFeedRequestDto)));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newsFeedResponseDto.getId()))
                .andExpect(jsonPath("$.authorId").value(newsFeedResponseDto.getAuthorId()))
                .andExpect(jsonPath("$.content").value(newsFeedResponseDto.getContent()));
    }

    @Test
    void deleteNewsFeed_Success() throws Exception {
        // given
        when(newsFeedService.deleteNewsFeed(anyLong())).thenReturn("NewsFeed deleted successfully");

        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/newsfeed/{newsFeedID}", 1L));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string("NewsFeed deleted successfully"));
    }
}