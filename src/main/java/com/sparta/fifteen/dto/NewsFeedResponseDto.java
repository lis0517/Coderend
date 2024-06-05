package com.sparta.fifteen.dto;

import com.sparta.fifteen.entity.NewsFeed;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewsFeedResponseDto {
    private int id;
    private int authorId;
    private String content;

    public NewsFeedResponseDto(NewsFeed newsFeed) {
        this.id=newsFeed.getId();
        this.authorId=newsFeed.getAuthorId();
        this.content=newsFeed.getContent();
    }
}
