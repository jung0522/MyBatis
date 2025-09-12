package io.goorm.mybatisboard.service;

import io.goorm.mybatisboard.dto.PageDto;
import io.goorm.mybatisboard.dto.PostDetailDto;
import io.goorm.mybatisboard.dto.PostFormDto;
import io.goorm.mybatisboard.dto.PostListDto;
import io.goorm.mybatisboard.dto.SearchConditionDto;
import io.goorm.mybatisboard.dto.PostWithDetailsDto;
import io.goorm.mybatisboard.dto.CategoryDto;
import io.goorm.mybatisboard.mapper.PostMapper;
import io.goorm.mybatisboard.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostService {

    private final PostMapper postMapper;

    public PageDto<PostListDto> findAll(int page, int size) {
        log.debug("Finding posts with pagination: page={}, size={}", page, size);

        // 전체 데이터 수 조회
        int totalElements = postMapper.countAll();
        log.debug("Total posts count: {}", totalElements);

        // OFFSET 계산 (페이지는 1부터 시작)
        int offset = (page - 1) * size;

        // 페이징된 데이터 조회
        List<Post> posts = postMapper.findAll(offset, size);
        log.debug("Found {} posts for page {}", posts.size(), page);

        List<PostListDto> postListDtos = posts.stream()
                .map(this::convertToListDto)
                .collect(Collectors.toList());

        return PageDto.of(postListDtos, page, size, totalElements);
    }

    public PageDto<PostListDto> findAll(int page, int size, String searchType, String keyword) {
        log.debug("Finding posts with search: page={}, size={}, searchType={}, keyword={}", page, size, searchType, keyword);

        // 검색 조건이 없으면 기본 조회
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll(page, size);
        }

        // 검색어 전처리
        String trimmedKeyword = keyword.trim();

        // 검색 조건에 따른 전체 데이터 수 조회
        int totalElements = postMapper.countAllWithSearch(searchType, trimmedKeyword);
        log.debug("Total search results count: {} for keyword: {}", totalElements, trimmedKeyword);

        // OFFSET 계산 (페이지는 1부터 시작)
        int offset = (page - 1) * size;

        // 검색 조건에 따른 페이징된 데이터 조회
        List<Post> posts = postMapper.findAllWithSearch(searchType, trimmedKeyword, offset, size);
        log.debug("Found {} search results for page {}", posts.size(), page);

        List<PostListDto> postListDtos = posts.stream()
                .map(this::convertToListDto)
                .collect(Collectors.toList());

        return PageDto.of(postListDtos, page, size, totalElements);
    }

    public PostDetailDto findBySeq(Long seq) {
        log.debug("Finding post by seq: {}", seq);
        Post post = postMapper.findById(seq);
        if (post == null) {
            log.error("Post not found with seq: {}", seq);
            throw new RuntimeException("게시글을 찾을 수 없습니다. ID: " + seq);
        }
        log.debug("Found post: {} (seq: {})", post.getTitle(), seq);
        return convertToDetailDto(post);
    }

    @Transactional
    public Post save(PostFormDto postFormDto) {
        log.debug("Saving new post with title: {}", postFormDto.getTitle());
        Post post = new Post();
        post.setTitle(postFormDto.getTitle());
        post.setContent(postFormDto.getContent());
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        // 확장 필드 설정 (폼에서 입력받거나 기본값)
        post.setCategoryId(postFormDto.getCategoryId() != null ? postFormDto.getCategoryId() : 2L);
        post.setStatus("PUBLISHED");
        post.setAuthorName(postFormDto.getAuthorName() != null && !postFormDto.getAuthorName().trim().isEmpty()
                ? postFormDto.getAuthorName() : "작성자");
        post.setViewCount(0);
        post.setIsNotice(postFormDto.getIsNotice() != null ? postFormDto.getIsNotice() : false);

        postMapper.save(post);
        log.info("Post saved successfully with title: {}", postFormDto.getTitle());
        return post;
    }

    @Transactional
    public Post update(Long seq, PostFormDto postFormDto) {
        log.debug("Updating post seq: {} with title: {}", seq, postFormDto.getTitle());
        Post existingPost = postMapper.findById(seq);
        if (existingPost == null) {
            log.error("Post not found for update with seq: {}", seq);
            throw new RuntimeException("게시글을 찾을 수 없습니다. ID: " + seq);
        }

        Post updatePost = new Post();
        updatePost.setTitle(postFormDto.getTitle());
        updatePost.setContent(postFormDto.getContent());
        updatePost.setUpdatedAt(LocalDateTime.now());

        // 확장 필드 설정 (폼에서 입력받거나 기존값 유지)
        updatePost.setCategoryId(postFormDto.getCategoryId() != null ? postFormDto.getCategoryId() : existingPost.getCategoryId());
        updatePost.setStatus(existingPost.getStatus());
        updatePost.setAuthorName(postFormDto.getAuthorName() != null && !postFormDto.getAuthorName().trim().isEmpty()
                ? postFormDto.getAuthorName() : existingPost.getAuthorName());
        updatePost.setViewCount(existingPost.getViewCount());
        updatePost.setIsNotice(postFormDto.getIsNotice() != null ? postFormDto.getIsNotice() : existingPost.getIsNotice());

        postMapper.update(seq, updatePost);
        log.info("Post updated successfully seq: {}, title: {}", seq, postFormDto.getTitle());
        return postMapper.findById(seq);
    }

    @Transactional
    public void delete(Long seq) {
        log.debug("Deleting post seq: {}", seq);
        Post existingPost = postMapper.findById(seq);
        if (existingPost == null) {
            log.error("Post not found for deletion with seq: {}", seq);
            throw new RuntimeException("게시글을 찾을 수 없습니다. ID: " + seq);
        }
        postMapper.delete(seq);
        log.info("Post deleted successfully seq: {}, title: {}", seq, existingPost.getTitle());
    }

    private PostListDto convertToListDto(Post post) {
        PostListDto dto = new PostListDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setCreatedAt(post.getCreatedAt());

        dto.setCategoryId(post.getCategoryId());
        dto.setCategoryName(getCategoryName(post.getCategoryId()));
        dto.setStatus(post.getStatus());
        dto.setAuthorName(post.getAuthorName());
        dto.setViewCount(post.getViewCount());
        dto.setIsNotice(post.getIsNotice());

        return dto;
    }

    private String getCategoryName(Long categoryId) {
        if (categoryId == null) return "미분류";
        switch (categoryId.intValue()) {
            case 1: return "공지사항";
            case 2: return "일반";
            case 3: return "질문";
            case 4: return "정보공유";
            case 5: return "자유게시판";
            default: return "미분류";
        }
    }

    private PostDetailDto convertToDetailDto(Post post) {
        PostDetailDto dto = new PostDetailDto();
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());

        dto.setCategoryId(post.getCategoryId());
        dto.setCategoryName(getCategoryName(post.getCategoryId()));
        dto.setStatus(post.getStatus());
        dto.setAuthorName(post.getAuthorName());
        dto.setViewCount(post.getViewCount());
        dto.setIsNotice(post.getIsNotice());

        return dto;
    }

    // ========== 통합 검색 시스템 ==========

    public PageDto<PostWithDetailsDto> findAllWithConditions(SearchConditionDto condition) {
        log.debug("Finding posts with integrated search conditions: {}", condition.getSummary());

        condition.validateAndCorrect();

        int totalElements = postMapper.countAllWithConditions(condition);
        log.debug("Total posts count with conditions: {}", totalElements);

        List<PostWithDetailsDto> posts = postMapper.findAllWithConditions(condition);
        log.debug("Found {} posts for page {}", posts.size(), condition.getPage());

        return PageDto.of(posts, condition.getPage(), condition.getSize(), totalElements);
    }

    public List<CategoryDto> findAllCategories() {
        log.debug("Finding all categories");
        List<CategoryDto> categories = postMapper.findAllCategories();
        log.debug("Found {} categories", categories.size());
        return categories;
    }

    public List<CategoryDto> findActiveCategories() {
        log.debug("Finding active categories");
        List<CategoryDto> categories = postMapper.findActiveCategories();
        log.debug("Found {} active categories", categories.size());
        return categories;
    }
}