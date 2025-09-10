package io.goorm.mybatis_basic.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostFormDto {
    
    private String title;
    private String content;
}