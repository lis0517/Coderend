package com.sparta.fifteen.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsFeedRequestDto {
    private long id;
    private long authorId;
    @NotBlank(message ="내용을 입력해주세요.")
    private String content;
}
