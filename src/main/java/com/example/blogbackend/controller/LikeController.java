package com.example.blogbackend.controller;

import com.example.blogbackend.dto.LikeDto;
import com.example.blogbackend.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/board/like")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody LikeDto likeDto) {
        likeService.insert(likeDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestBody LikeDto likeDto) {
        likeService.delete(likeDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> stateCheck(@RequestParam("boardId") Long boardId,
                                              @RequestParam("userId") Long userId) {
        LikeDto likeDto = LikeDto.builder()
                .boardId(boardId)
                .userId(userId)
                .build();
        Boolean state = likeService.getStateCheck(likeDto);
        return ResponseEntity.ok(state);
    }

    @GetMapping("/count/{boardId}")
    public ResponseEntity<Long> getLikeCount(@PathVariable("boardId") Long boardId) {
        long likeCount = likeService.getLikeCount(boardId);
        return ResponseEntity.ok(likeCount);
    }
}
