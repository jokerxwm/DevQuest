package com.devquest.notification.service;

import com.devquest.common.result.PageResult;
import com.devquest.notification.entity.Notification;

import java.util.Map;

public interface NotificationService {

    Notification createNotification(Long receiverId, Long senderId, String type,
                                     String title, String content, Long targetId, String targetType);

    PageResult<Notification> getUserNotifications(Long userId, int page, int size, String type);

    long getUnreadCount(Long userId);

    void markAsRead(Long userId, Long notificationId);

    void markAllAsRead(Long userId);

    void deleteNotification(Long userId, Long notificationId);
}
