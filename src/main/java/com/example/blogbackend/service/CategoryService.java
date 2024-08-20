package com.example.blogbackend.service;

import com.example.blogbackend.dto.BoardDto;
import com.example.blogbackend.dto.LikeDto;
import com.example.blogbackend.entity.BoardEntity;
import com.example.blogbackend.entity.UserEntity;
import com.example.blogbackend.entity.model.Category;
import com.example.blogbackend.entity.model.Header;
import com.example.blogbackend.entity.model.Pagination;
import com.example.blogbackend.repository.BoardRepository;
import com.example.blogbackend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final BoardRepository boardRepository;
    private final UserService userService;
    private final LikeService likeService;

    /**
     * 카테고리로 게시글 가져오기
     */
    public Header<List<BoardDto>> getBoardsWithCategory(Pageable pageable, String category) {
        List<BoardDto> dtos = new ArrayList<>();
        Category category1 = categoryRepository.findCategoryByName(category);
        Page<BoardEntity> boardList = boardRepository.findByCategoryOrderByCreatedAtDesc(pageable, category1);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserEntity userEntity = userService.findByUserId(userDetails.getUsername());
            userId = userEntity.getIdx();
        }
        for (BoardEntity boardEntity : boardList) {
            Long likeCount = likeService.getLikeCount(boardEntity.getIdx());
            boolean isLiked = false;
            if (userId != null) {
                LikeDto likeDto = LikeDto.builder()
                        .boardId(boardEntity.getIdx())
                        .userId(userId)
                        .build();
                isLiked = likeService.getStateCheck(likeDto);
            }
            BoardDto dto = BoardDto.builder()
                    .idx(boardEntity.getIdx())
                    .author(boardEntity.getAuthor())
                    .title(boardEntity.getTitle())
                    .contents(boardEntity.getContents())
                    .category(boardEntity.getCategory())
                    .isLiked(isLiked)
                    .likeCount(likeCount)
                    .viewCount(boardEntity.getViewCount())
                    .createdAt(boardEntity.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")))
                    .build();

            dtos.add(dto);
        }

        Pagination pagination = new Pagination(
                (int) boardList.getTotalElements(),
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                10
        );

        return Header.OK(dtos, pagination);
    }

    public Category createCategory(String name) {
        Category category = Category.builder()
                .name(name).build();
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow();
    }
}
