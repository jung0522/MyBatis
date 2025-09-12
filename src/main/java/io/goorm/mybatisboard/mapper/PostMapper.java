package io.goorm.mybatisboard.mapper;

import io.goorm.mybatisboard.model.Post;
import io.goorm.mybatisboard.dto.SearchConditionDto;
import io.goorm.mybatisboard.dto.PostWithDetailsDto;
import io.goorm.mybatisboard.dto.CategoryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {

    List<Post> findAll(@Param("offset") int offset, @Param("size") int size);

    int countAll();

    List<Post> findAllWithSearch(@Param("searchType") String searchType,
                                 @Param("keyword") String keyword,
                                 @Param("offset") int offset,
                                 @Param("size") int size);

    int countAllWithSearch(@Param("searchType") String searchType,
                           @Param("keyword") String keyword);

    Post findById(@Param("id") Long id);
    
    void save(Post post);
    
    void update(@Param("id") Long id, @Param("post") Post post);
    
    void delete(@Param("id") Long id);
    
    // ========== 통합 검색 시스템 ==========
    
    List<PostWithDetailsDto> findAllWithConditions(SearchConditionDto condition);
    
    int countAllWithConditions(SearchConditionDto condition);
    
    // ========== 카테고리 관리 ==========
    
    List<CategoryDto> findAllCategories();
    
    List<CategoryDto> findActiveCategories();
}