package io.goorm.mybatisboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Category 전용 검색 조건 DTO
 * BaseSearchConditionDto를 상속받아 Category 고유 필드들을 추가
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CategorySearchConditionDto extends BaseSearchConditionDto {
    
    // ========== Category 고유 필드 ==========
    private Boolean isActive;                // 활성 상태
    private Integer minDisplayOrder;         // 최소 표시 순서
    private Integer maxDisplayOrder;         // 최대 표시 순서
    private String description;              // 설명 검색
    
    // ========== Category 고유 유틸리티 메서드 ==========
    
    /**
     * 활성 상태 필터 유효성 체크
     */
    public boolean hasActiveFilter() {
        return isActive != null;
    }
    
    /**
     * 표시 순서 범위 필터 유효성 체크
     */
    public boolean hasDisplayOrderFilter() {
        return minDisplayOrder != null || maxDisplayOrder != null;
    }
    
    /**
     * 설명 검색 필터 유효성 체크
     */
    public boolean hasDescriptionFilter() {
        return description != null && !description.trim().isEmpty();
    }
    
    /**
     * Category 검색 조건이 비어있는지 체크
     */
    @Override
    public boolean isEmpty() {
        return !hasKeyword() && 
               !hasActiveFilter() && 
               !hasDisplayOrderFilter() && 
               !hasDescriptionFilter();
    }
}