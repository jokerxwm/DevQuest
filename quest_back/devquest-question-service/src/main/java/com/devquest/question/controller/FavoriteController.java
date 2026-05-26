package com.devquest.question.controller;

import com.devquest.common.result.PageResult;
import com.devquest.common.result.R;
import com.devquest.common.util.UserContext;
import com.devquest.question.entity.Question;
import com.devquest.question.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{questionId}")
    public R<Void> addFavorite(@PathVariable Long questionId) {
        Long userId = UserContext.getRequiredUserId();
        favoriteService.addFavorite(userId, questionId);
        return R.ok();
    }

    @DeleteMapping("/{questionId}")
    public R<Void> removeFavorite(@PathVariable Long questionId) {
        Long userId = UserContext.getRequiredUserId();
        favoriteService.removeFavorite(userId, questionId);
        return R.ok();
    }

    @GetMapping("/{questionId}/status")
    public R<Map<String, Object>> getFavoriteStatus(@PathVariable Long questionId) {
        Long userId = UserContext.getUserId();
        boolean isFavorited = userId != null && favoriteService.isFavorited(userId, questionId);
        return R.ok(Map.of("isFavorited", isFavorited));
    }

    @GetMapping("/user/{userId}")
    public R<PageResult<Question>> getUserFavorites(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return R.ok(favoriteService.getUserFavorites(userId, page, size));
    }

    @GetMapping("/list")
    public R<PageResult<Question>> getMyFavorites(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = UserContext.getRequiredUserId();
        return R.ok(favoriteService.getUserFavorites(userId, page, size));
    }

    @GetMapping("/check/{questionId}")
    public R<Map<String, Object>> checkFavorited(@PathVariable Long questionId) {
        Long userId = UserContext.getUserId();
        boolean isFavorited = userId != null && favoriteService.isFavorited(userId, questionId);
        return R.ok(Map.of("isFavorited", isFavorited));
    }
}
