package com.sparta.fifteen.controller;

import com.sparta.fifteen.config.TestSecurityConfig;
import com.sparta.fifteen.entity.ContentTypeEnum;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.security.UserDetailsImpl;
import com.sparta.fifteen.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class LikeControllerTest {

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

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .build();
    }

    @Test
    void toggleLikeNewsFeed_Success() throws Exception {
        // given
        Long newsfeedId = 1L;
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/newsfeed/{newsfeedId}/likeToggle", newsfeedId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string("좋아요 토글 성공"))
                .andDo(print());

        verify(likeService).likeOrUnlike(user, newsfeedId, ContentTypeEnum.NEWSFEED_TYPE);
    }

    @Test
    void toggleLikeNewsFeed_Failure() throws Exception {
        // given
        Long newsfeedId = 1L;
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        doThrow(new RuntimeException("좋아요 토글 실패")).when(likeService)
                .likeOrUnlike(any(User.class), eq(newsfeedId), eq(ContentTypeEnum.NEWSFEED_TYPE));

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/newsfeed/{newsfeedId}/likeToggle", newsfeedId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string("좋아요 토글 실패"))
                .andDo(print());
    }

    @Test
    void toggleLikeComment_Success() throws Exception {
        // given
        Long commentId = 1L;
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/newsfeed/{newsfeedId}/comments/{commentId}/likeToggle", 1L, commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string("좋아요 토글 성공"))
                .andDo(print());

        verify(likeService).likeOrUnlike(user, commentId, ContentTypeEnum.COMMENT_TYPE);
    }

    @Test
    void toggleLikeComment_Failure() throws Exception {
        // given
        Long commentId = 1L;
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        doThrow(new RuntimeException("좋아요 토글 실패")).when(likeService)
                .likeOrUnlike(any(User.class), eq(commentId), eq(ContentTypeEnum.COMMENT_TYPE));

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/newsfeed/{newsfeedId}/comments/{commentId}/likeToggle", 1L, commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string("좋아요 토글 실패"))
                .andDo(print());
    }
}