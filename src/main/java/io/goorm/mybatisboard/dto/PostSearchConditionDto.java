package io.goorm.mybatisboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * Post 전용 검색 조건 DTO
 * BaseSearchConditionDto를 상속받아 Post 고유 필드들을 추가
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PostSearchConditionDto extends BaseSearchConditionDto {
    
    // ========== Post 고유 필드 ==========
    private List<Long> categoryIds;          // 카테고리 다중 선택
    private String status;                   // 게시글 상태 (PUBLISHED, DRAFT, DELETED)
    private String authorName;               // 작성자명
    private Boolean isNotice;                // 공지사항 여부
    
    // ========== 날짜 범위 검색 ==========
    private LocalDate startDate;             // 검색 시작일
    private LocalDate endDate;               // 검색 종료일
    
    // ========== Post 고유 유틸리티 메서드 ==========
    
    /**
     * 카테고리 필터 유효성 체크
     */
    public boolean hasCategoryFilter() {
        return categoryIds != null && !categoryIds.isEmpty();
    }
    
    /**
     * 상태 필터 유효성 체크
     */
    public boolean hasStatusFilter() {
        return status != null && !status.trim().isEmpty();
    }
    
    /**
     * 작성자 필터 유효성 체크
     */
    public boolean hasAuthorFilter() {
        return authorName != null && !authorName.trim().isEmpty();
    }
    
    /**
     * 날짜 범위 필터 유효성 체크
     */
    public boolean hasDateRangeFilter() {
        return startDate != null || endDate != null;
    }
    
    /**
     * Post 검색 조건이 비어있는지 체크
     */
    @Override
    public boolean isEmpty() {
        return !hasKeyword() && 
               !hasCategoryFilter() && 
               !hasStatusFilter() && 
               !hasAuthorFilter() && 
               isNotice == null && 
               !hasDateRangeFilter();
    }
    
    /**
     * 검색 타입 기본값 설정
     */
    public String getSearchType() {
        return (searchType != null && !searchType.trim().isEmpty()) ? searchType : "title_content";
    }
    
    /**
     * 카테고리 ID 리스트 Null-Safe 처리
     */
    public List<Long> getCategoryIds() {
        return categoryIds != null ? categoryIds : new ArrayList<>();
    }
    
    /**
     * 페이지 범위 체크 및 보정
     */
    public void validateAndCorrect() {
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        if (size > 100) size = 100;  // 최대 페이지 크기 제한
        
        // 정렬 옵션 유효성 검증
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "created_at";
        }
        if (!"ASC".equalsIgnoreCase(sortDirection) && !"DESC".equalsIgnoreCase(sortDirection)) {
            sortDirection = "DESC";
        }
    }
    
    /**
     * 검색 조건 요약 문자열 생성 (디버깅/로깅용)
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        if (hasKeyword()) sb.append("키워드:").append(keyword).append(" ");
        if (hasCategoryFilter()) sb.append("카테고리:").append(categoryIds.size()).append("개 ");
        if (hasStatusFilter()) sb.append("상태:").append(status).append(" ");
        if (hasAuthorFilter()) sb.append("작성자:").append(authorName).append(" ");
        if (isNotice != null) sb.append("공지:").append(isNotice).append(" ");
        if (hasDateRangeFilter()) sb.append("날짜범위:").append(startDate).append("~").append(endDate).append(" ");
        sb.append("정렬:").append(sortBy).append(" ").append(sortDirection);
        return sb.toString().trim();
    }
}
