package io.goorm.mybatisboard.controller;

import io.goorm.mybatisboard.dto.CategoryDto;
import io.goorm.mybatisboard.dto.CategorySearchConditionDto;
import io.goorm.mybatisboard.dto.PageDto;
import io.goorm.mybatisboard.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    /**
     * 카테고리 목록 페이지 (검색 + 정렬 + 페이징)
     */
    @GetMapping
    public String list(@ModelAttribute CategorySearchConditionDto condition, Model model) {
        // 빈 조건일 경우 기본 목록 조회
        if (condition.isEmpty()) {
            PageDto<CategoryDto> pageResult = categoryService.findAll(condition.getPage(), condition.getSize());
            model.addAttribute("pageResult", pageResult);
        } else {
            PageDto<CategoryDto> pageResult = categoryService.findAllWithConditions(condition);
            model.addAttribute("pageResult", pageResult);
        }
        
        model.addAttribute("condition", condition);
        return "category/list";
    }
    
    /**
     * 카테고리 검색 (GET 방식)
     */
    @GetMapping("/search")
    public String search(@ModelAttribute CategorySearchConditionDto condition, Model model) {
        PageDto<CategoryDto> pageResult = categoryService.findAllWithConditions(condition);
        
        model.addAttribute("pageResult", pageResult);
        model.addAttribute("condition", condition);
        return "category/list";
    }
}