package com.example.blogbackend.controller;

import com.example.blogbackend.dto.BoardDto;
import com.example.blogbackend.entity.BoardEntity;
import com.example.blogbackend.entity.model.Header;
import com.example.blogbackend.entity.model.SearchCondition;
import com.example.blogbackend.service.BoardService;
import com.example.blogbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin
@RestController
public class BoardController {

    private final BoardService boardService;
    private final UserService userService;

    @GetMapping("/board/list")
    public Header<List<BoardDto>> boardList(
            @PageableDefault(sort = {"idx"}) Pageable pageable,
            SearchCondition searchCondition
            ) {
        return boardService.getBoardList(pageable, searchCondition);
    }

    @GetMapping("/board/liked")
    public Header<List<BoardDto>> likedBoardList(
            @PageableDefault(sort = {"id"}) Pageable pageable, @RequestParam(value = "userId") String userId) {
        return boardService.getLikedBoardList(Long.valueOf(userId), pageable);
    }

    @GetMapping("/board/{id}")
    public BoardDto getBoard(@PathVariable("id") Long id) { return boardService.getBoard(id); }

    @PostMapping("/board")
    public BoardEntity create(@RequestBody BoardDto boardDto) throws IllegalAccessException {
        log.info("Received BoardDto : {}", boardDto);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String username = userService.findUserNameByUserId(userDetails.getUsername());
            boardDto.setAuthor(username);
        }
        return boardService.create(boardDto); }

    @PatchMapping("/board")
    public BoardEntity update(@RequestBody BoardDto boardDto) {
        return boardService.update(boardDto);
    }

    @DeleteMapping("/board/{id}")
    public void delete(@PathVariable("id") Long id) { boardService.delete(id); }

    @PostMapping("/board/{boardId}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable("boardId") Long boardId) {
        boardService.incrementViewCount(boardId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/board/{boardId}/view-count")
    public ResponseEntity<Long> getViewCount(@PathVariable("boardId") Long boardId) {
        Long viewCount = boardService.getViewCount(boardId);
        return ResponseEntity.ok(viewCount);
    }

    @GetMapping("/board-count-by-category")
    public ResponseEntity<Map<String, Long>> getBoardsCountByCategory() {
        Map<String, Long> postCountByCategory = boardService.getBoardsCountByCategory();
        return ResponseEntity.ok(postCountByCategory);
    }

    @GetMapping("/total-boards")
    public ResponseEntity<String> getTotalBoards() {
        return ResponseEntity.ok(boardService.getTotalBoards());
    }
}
