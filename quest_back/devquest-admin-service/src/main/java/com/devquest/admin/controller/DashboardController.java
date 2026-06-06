package com.devquest.admin.controller;

import com.devquest.admin.service.DashboardService;
import com.devquest.common.result.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public R<Map<String, Object>> getStats() {
        return R.ok(dashboardService.getStats());
    }
}
