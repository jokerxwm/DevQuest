package com.devquest.notification.controller;

import com.devquest.common.result.PageResult;
import com.devquest.common.result.R;
import com.devquest.common.util.UserContext;
import com.devquest.notification.entity.Notification;
import com.devquest.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public R<PageResult<Notification>> getNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type) {
        Long userId = UserContext.getRequiredUserId();
        return R.ok(notificationService.getUserNotifications(userId, page, size, type));
    }

    @GetMapping("/unread-count")
    public R<Map<String, Long>> getUnreadCount() {
        Long userId = UserContext.getRequiredUserId();
        long count = notificationService.getUnreadCount(userId);
        return R.ok(Map.of("count", count));
    }

    @PutMapping("/{notificationId}/read")
    public R<Void> markAsRead(@PathVariable Long notificationId) {
        Long userId = UserContext.getRequiredUserId();
        notificationService.markAsRead(userId, notificationId);
        return R.ok();
    }

    @PutMapping("/read-all")
    public R<Void> markAllAsRead() {
        Long userId = UserContext.getRequiredUserId();
        notificationService.markAllAsRead(userId);
        return R.ok();
    }

    @DeleteMapping("/{notificationId}")
    public R<Void> deleteNotification(@PathVariable Long notificationId) {
        Long userId = UserContext.getRequiredUserId();
        notificationService.deleteNotification(userId, notificationId);
        return R.ok();
    }
}
