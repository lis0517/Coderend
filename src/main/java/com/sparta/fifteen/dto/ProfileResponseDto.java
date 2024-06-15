package com.sparta.fifteen.dto;

import com.sparta.fifteen.entity.User;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProfileResponseDto {
    private String username;
    private String name;
    private String oneline;
    private String email;

    public ProfileResponseDto(User user) {
        this.username = user.getUsername();
        this.name = user.getName();
        this.oneline = user.getOneLine();
        this.email = user.getEmail();
    }

}
