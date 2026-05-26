package com.devquest.admin.controller;

import com.devquest.admin.entity.User;
import com.devquest.admin.service.AdminUserService;
import com.devquest.common.result.PageResult;
import com.devquest.common.result.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public R<PageResult<User>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return R.ok(adminUserService.getUsers(page, size, keyword, status));
    }

    @PutMapping("/{userId}/status")
    public R<Void> updateUserStatus(@PathVariable Long userId, @RequestBody Map<String, Integer> params) {
        adminUserService.updateUserStatus(userId, params.get("status"));
        return R.ok();
    }

    @PutMapping("/{userId}/role")
    public R<Void> updateUserRole(@PathVariable Long userId, @RequestBody Map<String, String> params) {
        adminUserService.updateUserRole(userId, params.get("role"));
        return R.ok();
    }
}
