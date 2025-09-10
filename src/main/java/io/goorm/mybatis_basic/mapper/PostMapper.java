package io.goorm.mybatis_basic.mapper;

import io.goorm.mybatis_basic.model.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {

    List<Post> findAll(@Param("offset") int offset, @Param("size") int size);

    int countAll();

    Post findById(@Param("id") Long id);

    void save(Post post);

    void update(@Param("id") Long id, @Param("post") Post post);

    void delete(@Param("id") Long id);
}