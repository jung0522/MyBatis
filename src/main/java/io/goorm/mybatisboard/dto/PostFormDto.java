package io.goorm.mybatisboard.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostFormDto {
    
    private String title;
    private String content;
    private Long categoryId;
    private String authorName;
    private Boolean isNotice;
}