package io.goorm.mybatisboard.mapper;

import io.goorm.mybatisboard.dto.CategoryDto;
import io.goorm.mybatisboard.dto.CategorySearchConditionDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {
    
    // ========== 검색 기능 ==========
    
    /**
     * 검색 조건에 따른 카테고리 목록 조회
     */
    List<CategoryDto> findAllWithConditions(CategorySearchConditionDto condition);
    
    /**
     * 검색 조건에 따른 카테고리 총 개수
     */
    int countAllWithConditions(CategorySearchConditionDto condition);
    
    // ========== 기본 조회 ==========
    
    /**
     * 모든 카테고리 조회 (페이징)
     */
    List<CategoryDto> findAll(@Param("offset") int offset, @Param("size") int size);
    
    /**
     * 활성 카테고리만 조회
     */
    List<CategoryDto> findActiveCategories();
    
    /**
     * 전체 카테고리 수
     */
    int countAll();
}