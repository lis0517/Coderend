package com.sparta.fifteen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.fifteen.dto.NewsFeedRequestDto;
import com.sparta.fifteen.dto.NewsFeedResponseDto;
import com.sparta.fifteen.entity.NewsFeed;
import com.sparta.fifteen.service.NewsFeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WebMvcTest
public class NewsFeedTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsFeedService newsFeedService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void getNewsFeedTest() throws Exception {
        // Given
        long newsFeedId = 1L;
        NewsFeedResponseDto newsFeedResponseDto = new NewsFeedResponseDto();
        newsFeedResponseDto.setContent("Test Content");

        when(newsFeedService.getNewsFeed(any(Long.class))).thenReturn(newsFeedResponseDto);

        // When & Then
        mockMvc.perform(get("/api/newsfeed/{id}", newsFeedId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(newsFeedResponseDto)));
    }

    @Test
    public void updateNewsFeedTest() throws Exception {
        NewsFeedRequestDto newsFeedRequestDto = new NewsFeedRequestDto();
        NewsFeedResponseDto newsFeedResponseDto = new NewsFeedResponseDto();
        when(newsFeedService.getNewsFeed(any())).thenReturn(newsFeedResponseDto);
        mockMvc.perform(put("/api/newsfeed/1")
                        .param("id", String.valueOf(newsFeedRequestDto.getId()))
                        .content(objectMapper.writeValueAsString(newsFeedRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(newsFeedResponseDto)));
    }
}
