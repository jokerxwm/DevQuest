package com.devquest.user.controller;

import com.devquest.common.result.R;
import com.devquest.common.util.UserContext;
import com.devquest.user.entity.User;
import com.devquest.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public R<User> getUserInfo(@PathVariable Long userId) {
        return R.ok(userService.getUserInfo(userId));
    }

    @GetMapping("/username/{username}")
    public R<User> getUserByUsername(@PathVariable String username) {
        return R.ok(userService.getUserByUsername(username));
    }

    @PutMapping("/profile")
    public R<Void> updateProfile(@RequestBody User user) {
        Long userId = UserContext.getRequiredUserId();
        userService.updateProfile(userId, user);
        return R.ok();
    }

    @PostMapping("/{userId}/follow")
    public R<Void> followUser(@PathVariable Long userId) {
        Long currentUserId = UserContext.getRequiredUserId();
        userService.followUser(currentUserId, userId);
        return R.ok();
    }

    @DeleteMapping("/{userId}/follow")
    public R<Void> unfollowUser(@PathVariable Long userId) {
        Long currentUserId = UserContext.getRequiredUserId();
        userService.unfollowUser(currentUserId, userId);
        return R.ok();
    }

    @GetMapping("/{userId}/follow-status")
    public R<Map<String, Object>> getFollowStatus(@PathVariable Long userId) {
        Long currentUserId = UserContext.getUserId();
        Map<String, Object> result = new HashMap<>();
        if (currentUserId != null) {
            result.put("isFollowing", userService.isFollowing(currentUserId, userId));
        } else {
            result.put("isFollowing", false);
        }
        result.put("followerCount", userService.getFollowerCount(userId));
        result.put("followingCount", userService.getFollowingCount(userId));
        return R.ok(result);
    }
}
