package io.goorm.mybatisboard.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SearchDto {
    
    private String searchType;  // title, content, title_content
    private String keyword;     // 검색 키워드
    
    // 검색 조건이 있는지 확인
    public boolean hasSearchCondition() {
        return keyword != null && !keyword.trim().isEmpty();
    }
    
    // 검색 타입 기본값 설정
    public String getSearchType() {
        return searchType != null ? searchType : "title_content";
    }
}