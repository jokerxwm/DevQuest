package com.devquest.user.controller;

import com.devquest.common.dto.LoginDTO;
import com.devquest.common.dto.RegisterDTO;
import com.devquest.common.result.R;
import com.devquest.common.util.UserContext;
import com.devquest.user.entity.User;
import com.devquest.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public R<Map<String, Object>> register(@Valid @RequestBody RegisterDTO dto) {
        return R.ok(userService.register(dto));
    }

    @PostMapping("/login")
    public R<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        return R.ok(userService.login(dto));
    }

    @PostMapping("/logout")
    public R<Void> logout() {
        userService.logout();
        return R.ok();
    }

    @GetMapping("/me")
    public R<User> getCurrentUser() {
        Long userId = UserContext.getRequiredUserId();
        return R.ok(userService.getUserInfo(userId));
    }
}
