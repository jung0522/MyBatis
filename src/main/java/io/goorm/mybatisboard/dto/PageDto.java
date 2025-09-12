package io.goorm.mybatisboard.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PageDto<T> {
    
    private List<T> content;        // 현재 페이지 데이터
    private int currentPage;        // 현재 페이지 번호 (1부터 시작)
    private int size;               // 페이지당 데이터 수
    private long totalElements;     // 전체 데이터 수
    private int totalPages;         // 전체 페이지 수
    
    // 계산된 속성들
    public boolean hasNext() {
        return currentPage < totalPages;
    }
    
    public boolean hasPrevious() {
        return currentPage > 1;
    }
    
    public boolean isFirst() {
        return currentPage == 1;
    }
    
    public boolean isLast() {
        return currentPage == totalPages;
    }
    
    public int getStartPage() {
        int start = Math.max(1, currentPage - 2);
        return Math.min(start, Math.max(1, totalPages - 4));
    }
    
    public int getEndPage() {
        int end = Math.min(totalPages, currentPage + 2);
        return Math.max(end, Math.min(5, totalPages));
    }
    
    // 정적 팩토리 메서드
    public static <T> PageDto<T> of(List<T> content, int currentPage, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PageDto<>(content, currentPage, size, totalElements, totalPages);
    }
}