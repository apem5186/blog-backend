package com.example.blogbackend.controller;

import com.example.blogbackend.dto.BoardDto;
import com.example.blogbackend.entity.model.Category;
import com.example.blogbackend.entity.model.Header;
import com.example.blogbackend.entity.model.SearchCondition;
import com.example.blogbackend.service.BoardService;
import com.example.blogbackend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/board/list/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody String name) {
        Category category = categoryService.createCategory(name);
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long id) {
        log.info("Deleted Category Name : {}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public Header<List<BoardDto>> boardList(
            @PageableDefault(sort = {"idx"}) Pageable pageable,
            SearchCondition searchCondition
    ) { return boardService.getBoardList(pageable, searchCondition); }

    @GetMapping("/{category}")
    public Header<List<BoardDto>> getBoardsWithCategory(@PageableDefault(sort = {"idx"}) Pageable pageable,
                                                        @PathVariable("category") String category) {
        return categoryService.getBoardsWithCategory(pageable, category);
    }

    @GetMapping("/categoryList")
    public List<Category> categoryList() {
        return categoryService.getAllCategories();
    }
}
