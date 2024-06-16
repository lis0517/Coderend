package com.sparta.fifteen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.fifteen.config.TestSecurityConfig;
import com.sparta.fifteen.dto.CommentRequestDto;
import com.sparta.fifteen.dto.CommentResponseDto;
import com.sparta.fifteen.dto.NewsFeedRequestDto;
import com.sparta.fifteen.entity.NewsFeed;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.security.UserDetailsImpl;
import com.sparta.fifteen.service.*;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class CommentControllerTest {


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

    private User createUser() {
        return User.builder()
                .id(1L)
                .username("testuser1234")
                .password("abcd1234")
                .name("le")
                .oneLine("hi")
                .build();
    }

    private NewsFeed createNewsFeed(Long newsFeedId) {
        return NewsFeed.builder()
                .id(newsFeedId)
                .likes(0L)
                .content("test content")
                .build();
    }

    private CommentRequestDto createCommentRequestDto(String content) {
        return CommentRequestDto.builder()
                .comment(content)
                .build();
    }

    private CommentResponseDto createCommentResponseDto(Long id, String content, Long newsFeedId) {
        return CommentResponseDto.builder()
                .id(id)
                .comment(content)
                .like(0L)
                .userId(1L)
                .newsFeedId(newsFeedId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }


    @Test
    public void createComment() throws Exception {

        // given
        Long newsFeedId = 1L;
        String content = "test content";

        User user = createUser();
        CommentRequestDto requestDto = createCommentRequestDto(content);
        NewsFeed newsFeed = createNewsFeed(newsFeedId);
        CommentResponseDto responseDto = createCommentResponseDto(1L, content, newsFeedId);

        given(newsFeedService.findNewsFeedById(newsFeedId)).willReturn(newsFeed);

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        //when
        when(commentService.createComment(any(User.class), any(NewsFeed.class), any(CommentRequestDto.class))).thenReturn(responseDto);

        ResultActions resultActions = mockMvc.perform(post("/api/comments/{newsfeedId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .content(objectMapper.writeValueAsString(requestDto)));

        //then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.comment").value(responseDto.getComment()))
                .andExpect(jsonPath("$.like").value(responseDto.getLike()))
                .andExpect(jsonPath("$.userId").value(responseDto.getUserId()))
                .andExpect(jsonPath("$.newsFeedId").value(responseDto.getNewsFeedId()))
                .andDo(print());
    }

    @Test
    public void getComments() throws Exception {
        // given
        Long newsFeedId = 1L;
        NewsFeed newsFeed = createNewsFeed(newsFeedId);
        List<CommentResponseDto> responseDtos = Arrays.asList(
                createCommentResponseDto(1L, "comment1", newsFeedId),
                createCommentResponseDto(2L, "comment2", newsFeedId)
        );

        given(newsFeedService.findNewsFeedById(newsFeedId)).willReturn(newsFeed);
        given(commentService.getComments(newsFeed)).willReturn(responseDtos);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/comments/{newsfeedId}", newsFeedId));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDtos.get(0).getId()))
                .andExpect(jsonPath("$[0].comment").value(responseDtos.get(0).getComment()))
                .andExpect(jsonPath("$[0].like").value(responseDtos.get(0).getLike()))
                .andExpect(jsonPath("$[0].userId").value(responseDtos.get(0).getUserId()))
                .andExpect(jsonPath("$[0].newsFeedId").value(responseDtos.get(0).getNewsFeedId()))
                .andExpect(jsonPath("$[1].id").value(responseDtos.get(1).getId()))
                .andExpect(jsonPath("$[1].comment").value(responseDtos.get(1).getComment()))
                .andExpect(jsonPath("$[1].like").value(responseDtos.get(1).getLike()))
                .andExpect(jsonPath("$[1].userId").value(responseDtos.get(1).getUserId()))
                .andExpect(jsonPath("$[1].newsFeedId").value(responseDtos.get(1).getNewsFeedId()));
    }

    @Test
    public void updateComment() throws Exception {
        // given
        User user = createUser();
        Long commentId = 1L;
        String updatedContent = "updated content";
        CommentRequestDto requestDto = createCommentRequestDto(updatedContent);
        CommentResponseDto responseDto = createCommentResponseDto(commentId, updatedContent, 1L);

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        // when
        when(commentService.updateComment(any(User.class), eq(commentId), any(CommentRequestDto.class))).thenReturn(responseDto);

        ResultActions resultActions = mockMvc.perform(put("/api/comments/{commentId}", commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .content(objectMapper.writeValueAsString(requestDto)));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.comment").value(responseDto.getComment()))
                .andExpect(jsonPath("$.like").value(responseDto.getLike()))
                .andExpect(jsonPath("$.userId").value(responseDto.getUserId()))
                .andExpect(jsonPath("$.newsFeedId").value(responseDto.getNewsFeedId()));
    }


    @Test
    public void deleteComment() throws Exception {
        // given
        User user = createUser();
        Long commentId = 1L;
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/comments/{commentId}", commentId)
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string("댓글 삭제에 성공했습니다."));

        verify(commentService).deleteComment(user, commentId);
    }

}