package io.goorm.mybatisboard.controller;

import io.goorm.mybatisboard.dto.PageDto;
import io.goorm.mybatisboard.dto.PostDetailDto;
import io.goorm.mybatisboard.dto.PostFormDto;
import io.goorm.mybatisboard.dto.PostListDto;
import io.goorm.mybatisboard.dto.SearchConditionDto;
import io.goorm.mybatisboard.dto.PostWithDetailsDto;
import io.goorm.mybatisboard.dto.CategoryDto;
import io.goorm.mybatisboard.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    // 메인 페이지
    @GetMapping("/")
    public String home() {
        log.info("Accessing home page");
        return "index";
    }

    // 게시글 목록 (페이징 + 검색)
    @GetMapping("/posts")
    public String list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            Model model) {
        log.info("Accessing posts list page with page={}, size={}, searchType={}, keyword={}",
                page, size, searchType, keyword);

        PageDto<PostListDto> pageResult;

        // 검색 조건이 있으면 검색, 없으면 전체 조회
        if (keyword != null && !keyword.trim().isEmpty()) {
            pageResult = postService.findAll(page, size, searchType, keyword);
            log.debug("Found {} search results on page {}/{} for keyword: {}",
                    pageResult.getContent().size(), page, pageResult.getTotalPages(), keyword);
        } else {
            pageResult = postService.findAll(page, size);
            log.debug("Found {} posts on page {}/{}",
                    pageResult.getContent().size(), page, pageResult.getTotalPages());
        }

        // 검색 조건 객체 생성
        SearchConditionDto condition = new SearchConditionDto();
        condition.setPage(page);
        condition.setSize(size);
        condition.setSearchType(searchType);
        condition.setKeyword(keyword);

        model.addAttribute("pageResult", pageResult);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("condition", condition);

        return "post/list";
    }

    // ========== 통합 검색 엔드포인트 ==========
    @GetMapping("/posts/search")
    public String searchWithConditions(SearchConditionDto condition, Model model) {
        log.info("Accessing integrated search with conditions: {}", condition.getSummary());

        PageDto<PostWithDetailsDto> pageResult = postService.findAllWithConditions(condition);
        List<CategoryDto> categories = postService.findActiveCategories();

        log.debug("Found {} posts on page {}/{} with conditions",
                pageResult.getContent().size(), condition.getPage(), pageResult.getTotalPages());

        model.addAttribute("pageResult", pageResult);
        model.addAttribute("condition", condition);
        model.addAttribute("categories", categories);

        return "post/search";
    }

    // 게시글 상세 조회
    @GetMapping("/posts/{seq}")
    public String show(@PathVariable Long seq, Model model) {
        log.info("Accessing post detail page for seq: {}", seq);
        PostDetailDto post = postService.findBySeq(seq);
        log.debug("Found post: {}", post.getTitle());
        model.addAttribute("post", post);
        model.addAttribute("seq", seq); // 수정/삭제를 위한 seq 전달
        return "post/show";
    }

    // 게시글 작성 폼
    @GetMapping("/posts/new")
    public String createForm(Model model) {
        log.info("Accessing new post form");
        model.addAttribute("post", new PostFormDto());
        return "post/form";
    }

    // 게시글 저장 → 목록으로
    @PostMapping("/posts")
    public String create(@ModelAttribute PostFormDto post,
                         RedirectAttributes redirectAttributes) {
        log.info("Creating new post with title: {}", post.getTitle());

        postService.save(post);
        log.info("Post created successfully: {}", post.getTitle());
        redirectAttributes.addFlashAttribute("message", "flash.post.created");
        return "redirect:/posts";
    }

    // 게시글 수정 폼
    @GetMapping("/posts/{seq}/edit")
    public String editForm(@PathVariable Long seq, Model model) {
        log.info("Accessing edit form for post seq: {}", seq);
        PostDetailDto post = postService.findBySeq(seq);

        // PostDetailDto를 PostFormDto로 변환
        PostFormDto formDto = new PostFormDto();
        formDto.setTitle(post.getTitle());
        formDto.setContent(post.getContent());
        formDto.setCategoryId(post.getCategoryId());
        formDto.setAuthorName(post.getAuthorName());
        formDto.setIsNotice(post.getIsNotice());

        log.debug("Loading post for edit: {}", post.getTitle());
        model.addAttribute("post", formDto);
        model.addAttribute("seq", seq);
        return "post/form";
    }

    // 게시글 수정 → 상세보기로
    @PostMapping("/posts/{seq}")
    public String update(@PathVariable Long seq,
                         @ModelAttribute PostFormDto post,
                         RedirectAttributes redirectAttributes) {
        log.info("Updating post seq: {} with title: {}", seq, post.getTitle());

        postService.update(seq, post);
        log.info("Post updated successfully seq: {}", seq);
        redirectAttributes.addFlashAttribute("message", "flash.post.updated");
        return "redirect:/posts/" + seq;
    }

    // 게시글 삭제 → 목록으로
    @PostMapping("/posts/{seq}/delete")
    public String delete(@PathVariable Long seq, RedirectAttributes redirectAttributes) {
        log.info("Deleting post seq: {}", seq);
        postService.delete(seq);
        log.info("Post deleted successfully seq: {}", seq);
        redirectAttributes.addFlashAttribute("message", "flash.post.deleted");
        return "redirect:/posts";
    }
}