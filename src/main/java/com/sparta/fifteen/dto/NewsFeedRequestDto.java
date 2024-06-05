package com.sparta.fifteen.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsFeedRequestDto {
    private int id;
    private int authorId;
    @NotBlank(message ="내용을 입력해주세요.")
    private String content;
}
