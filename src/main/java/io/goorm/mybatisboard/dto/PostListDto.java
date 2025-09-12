package io.goorm.mybatisboard.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostListDto {
    
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private Long categoryId;
    private String categoryName;
    private String status;
    private String authorName;
    private Integer viewCount;
    private Boolean isNotice;
}